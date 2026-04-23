import geopandas as gpd
import matplotlib.pyplot as plt
from pyproj import CRS
import rasterio
from shapely.geometry import box, Polygon, MultiPolygon
from shapely.ops import unary_union

# Tool Hyperparameters
MIN_CELL_SIZE = 500  # TODO: Specify minimum cell size in meters
POP_EMP_THRESHOLD = 15000  # TODO: Specify population + employment threshold to bunch zones together
CRS_TARGET = "EPSG:3067"    # TODO: Specify context-specific CRS

# Load population and employment rasters
# Load population and employment data from raster files. Rasters are grid-based
# datasets where each pixel holds a value (in this case, population or employment).
with rasterio.open("../rasterizationData/population_100m.tif") as pop_src:
    pop_data = pop_src.read(1)
    pop_transform = pop_src.transform
    pop_crs = CRS.from_user_input(pop_src.crs)

with rasterio.open("../rasterizationData/employment_100m.tif") as emp_src:
    emp_data = emp_src.read(1)

# Check raster information
# Combine population and employment data into a single raster.
pop_emp_data = pop_data + emp_data
rows, cols = pop_emp_data.shape

# Determine bounds of the study area
minx, miny = pop_transform * (0, rows)
maxx, maxy = pop_transform * (cols, 0)

# Optional print checks:
# print(rows)
# print(cols)

# Define a recursive function for quadtree subdivision.
def subdivide_cell(cell_geom, transform, min_size, threshold):
    x0, y0 = ~transform * (cell_geom.bounds[0], cell_geom.bounds[3])
    x1, y1 = ~transform * (cell_geom.bounds[2], cell_geom.bounds[1])
    x0, x1 = int(x0), int(x1)
    y0, y1 = int(y0), int(y1)

    if x0 < 0 or y0 < 0 or x1 > cols or y1 > rows:
        return []

    # Calculate the total population and employment for the current cell.
    sub_pop = pop_data[y0:y1, x0:x1].sum()
    sub_emp = emp_data[y0:y1, x0:x1].sum()
    total = sub_pop + sub_emp

    # Stop subdividing if the total population/employment is below the threshold or the cell has reached the minimum size.
    if total <= threshold or (cell_geom.bounds[2] - cell_geom.bounds[0] <= min_size):
        return [{
            "geometry": cell_geom,
            "population": sub_pop,
            "employment": sub_emp,
            "pop_emp": total
        }]
    else:
        # Otherwise, divide the current cell into four equal sub-cells and recursively call the function for each.
        minx, miny, maxx, maxy = cell_geom.bounds
        midx, midy = (minx + maxx) / 2, (miny + maxy) / 2
        sub_boxes = [
            box(minx, miny, midx, midy),
            box(midx, miny, maxx, midy),
            box(minx, midy, midx, maxy),
            box(midx, midy, maxx, maxy)
        ]
        cells = []
        for sub_box in sub_boxes:
            cells.extend(subdivide_cell(sub_box, transform, min_size, threshold))
        return cells

# Start the quadtree subdivision with a single large cell covering the entire study area.
initial_geom = box(minx, miny, maxx, maxy)
cells = subdivide_cell(initial_geom, pop_transform, MIN_CELL_SIZE, POP_EMP_THRESHOLD)

# Create a GeoDataFrame from the resulting quadtree cells.
gdf = gpd.GeoDataFrame(cells, crs=CRS_TARGET)
print(f"Total number of quadtree cells: {len(gdf)}")
gdf.plot(figsize=(100, 100), edgecolor='black')
plt.title("Recursive Quadtree Subdivision")
plt.show()

# Load zonal data, which will be used to constrain the final zones.
postcode_gdf = gpd.read_file("../rasterizationData/zonesFile.gpkg").to_crs(CRS_TARGET)

# Define a function to split the quadtree cells based on the postal code boundaries.
def split_raster_cells_by_regions(quadtree_gdf, region_gdf, pop_data, emp_data, transform, cols, rows):
    split_cells = []

    # Iterate through each postal code region.
    for _, region in region_gdf.iterrows():
        region_geom = region.geometry
        # Select quadtree cells that intersect with the current region.
        region_cells = quadtree_gdf[quadtree_gdf.intersects(region_geom)].copy()

        # Iterate through the intersecting quadtree cells.
        for _, cell in region_cells.iterrows():
            # If the cell is not entirely contained within the region, split it.
            if not region_geom.contains(cell.geometry):
                # Intersect the cell with the region to get the new, smaller geometry.
                intersection = cell.geometry.intersection(region_geom)
                if isinstance(intersection, (Polygon, MultiPolygon)):
                    intersections = [intersection] if isinstance(intersection, Polygon) else list(intersection.geoms)
                    for sub_geom in intersections:
                        # Recalculate population and employment for the new, smaller sub-geometry.
                        x0, y0 = ~transform * (sub_geom.bounds[0], sub_geom.bounds[3])
                        x1, y1 = ~transform * (sub_geom.bounds[2], sub_geom.bounds[1])
                        x0, x1 = int(x0), int(x1)
                        y0, y1 = int(y0), int(y1)

                        if x0 < 0 or y0 < 0 or x1 > cols or y1 > rows:
                            continue

                        sub_pop = pop_data[y0:y1, x0:x1].sum()
                        sub_emp = emp_data[y0:y1, x0:x1].sum()
                        total = sub_pop + sub_emp

                        split_cells.append({
                            "geometry": sub_geom,
                            "population": sub_pop,
                            "employment": sub_emp,
                            "pop_emp": total,
                            "postcode": region.get("postcode", "unknown")
                        })
            # If the cell is fully contained, just add it with the postal code.
            else:
                cell_dict = cell.to_dict()
                cell_dict["postcode"] = region.get("postcode", "unknown")
                split_cells.append(cell_dict)

    return gpd.GeoDataFrame(split_cells, crs=quadtree_gdf.crs)

# Split the quadtree cells using the postal code boundaries.
split_gdf = split_raster_cells_by_regions(
    quadtree_gdf=gdf,  # your quadtree output
    region_gdf=postcode_gdf,
    pop_data=pop_data,
    emp_data=emp_data,
    transform=pop_transform,
    cols=cols,
    rows=rows
)

print(f"Split cells: {len(split_gdf)}")
split_gdf.plot(figsize=(100, 100), edgecolor='black')
plt.title("Split Raster Cells by Region Boundaries")
plt.show()

# Define functions for merging the split cells.
def is_merge_valid(cell_a, cell_b, threshold):
    combined_value = cell_a['pop_emp'] + cell_b['pop_emp']
    # Do not merge if the combined value exceeds the threshold.
    if combined_value > threshold:
        return False

    merged_geom = unary_union([cell_a['geometry'], cell_b['geometry']])
    # Check for topological validity of the merged geometry.
    if not merged_geom.is_valid or not merged_geom.is_simple:
        return False

    # Ensure the merged geometry is a single, valid polygon.
    centroid = merged_geom.centroid
    if not merged_geom.contains(centroid):
        return False

    return True

def find_best_neighbour(target_cell, candidate_cells, threshold):
    best_cell = None
    max_shared_boundary = 0

    # Find the best neighbor to merge with based on the longest shared boundary.
    for candidate in candidate_cells:
        if is_merge_valid(target_cell, candidate, threshold):
            shared_boundary = target_cell['geometry'].intersection(candidate['geometry']).length
            if shared_boundary > max_shared_boundary:
                max_shared_boundary = shared_boundary
                best_cell = candidate

    return best_cell

def merge_raster_cells(region_cells, merge_set, threshold):
    region_cells = list(region_cells)
    merge_set = list(merge_set)

    # Iteratively merge cells until no more valid merges can be made.
    while merge_set:
        cell = merge_set.pop(0)

        if not region_cells and len(merge_set) == 0:
            region_cells.append(cell)
            break

        # Find the best neighbor to merge with.
        neighbour = find_best_neighbour(cell, region_cells, threshold)

        if neighbour:
            # If a valid neighbor is found, merge the two cells.
            merged_geom = unary_union([cell['geometry'], neighbour['geometry']])
            merged_cell = {
                'geometry': merged_geom,
                'population': cell['population'] + neighbour['population'],
                'employment': cell['employment'] + neighbour['employment'],
                'pop_emp': cell['pop_emp'] + neighbour['pop_emp']
            }

            # Remove the old cells and add the new, merged cell to the merge set.
            region_cells = [c for c in region_cells if c != neighbour]
            merge_set.append(merged_cell)
        else:
            # If no valid neighbor is found, the cell is added to the final list.
            region_cells.append(cell)

    return region_cells

# Iterate through each postal code group and merge cells within that group.
final_zones = []

for postcode, group in split_gdf.groupby("postcode"):
    region_cells = []
    merge_set = group.to_dict("records")

    merged = merge_raster_cells(region_cells, merge_set, threshold=MERGE_THRESHOLD)

    for cell in merged:
        cell["postcode"] = postcode
        final_zones.append(cell)

print(f"Final cells: {len(final_gdf)}")
final_gdf = gpd.GeoDataFrame(final_zones, crs=split_gdf.crs)
final_gdf.plot(figsize=(100, 100), edgecolor='black')
plt.title("Final TAZ Zones After Merge")
plt.show()

# Export the final TAZ zones to a GeoPackage file.
output_file = "tazs.gpkg"
layer_name = "taz_zones"

# Export to GeoPackage
final_gdf.to_file(output_file, layer=layer_name, driver="GPKG")
print(f"Final TAZ zones successfully exported to '{output_file}' as layer '{layer_name}'.")