package com.pb.sawdust.tensor;

import com.pb.sawdust.tensor.decorators.primitive.size.*;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.util.JavaType;

import java.util.Arrays;

/**
 * This class holds static methods for dealing with numeric tensors. 
 *
 * @author crf <br/>
 *         Started: Aug 19, 2009 1:13:01 PM
 */
class NumericTensor {

    static DoubleTensor asDoubleTensor(Tensor<? extends Number> tensor) {
        return asDoubleTensor(tensor,false);
    }

    private static DoubleTensor asDoubleTensor(Tensor<? extends Number> tensor, boolean wrappingWrapped) {
        try {
            switch (tensor.getType()) {
                case BYTE :
                case SHORT :
                case INT : return asDoubleTensor(asLongTensor(tensor),true);
                case LONG : {
                    try {
                        switch (tensor.size()) {
                            case 0 : return new LongAsDoubleD0Tensor((LongD0Tensor) tensor,wrappingWrapped);
                            case 1 : return new LongAsDoubleD1Tensor((LongD1Tensor) tensor,wrappingWrapped);
                            case 2 : return new LongAsDoubleD2Tensor((LongD2Tensor) tensor,wrappingWrapped);
                            case 3 : return new LongAsDoubleD3Tensor((LongD3Tensor) tensor,wrappingWrapped);
                            case 4 : return new LongAsDoubleD4Tensor((LongD4Tensor) tensor,wrappingWrapped);
                            case 5 : return new LongAsDoubleD5Tensor((LongD5Tensor) tensor,wrappingWrapped);
                            case 6 : return new LongAsDoubleD6Tensor((LongD6Tensor) tensor,wrappingWrapped);
                            case 7 : return new LongAsDoubleD7Tensor((LongD7Tensor) tensor,wrappingWrapped);
                            case 8 : return new LongAsDoubleD8Tensor((LongD8Tensor) tensor,wrappingWrapped);
                            case 9 : return new LongAsDoubleD9Tensor((LongD9Tensor) tensor,wrappingWrapped);
                            default : return new LongAsDoubleTensor((LongTensor) tensor,wrappingWrapped);
                        }
                    } catch (ClassCastException e) {
                        //not explicitly sized
                        switch (tensor.size()) {
                            case 0 : return new DoubleD0TensorShell(new LongAsDoubleTensor((LongTensor) tensor,wrappingWrapped));
                            case 1 : return new DoubleD1TensorShell(new LongAsDoubleTensor((LongTensor) tensor,wrappingWrapped));
                            case 2 : return new DoubleD2TensorShell(new LongAsDoubleTensor((LongTensor) tensor,wrappingWrapped));
                            case 3 : return new DoubleD3TensorShell(new LongAsDoubleTensor((LongTensor) tensor,wrappingWrapped));
                            case 4 : return new DoubleD4TensorShell(new LongAsDoubleTensor((LongTensor) tensor,wrappingWrapped));
                            case 5 : return new DoubleD5TensorShell(new LongAsDoubleTensor((LongTensor) tensor,wrappingWrapped));
                            case 6 : return new DoubleD6TensorShell(new LongAsDoubleTensor((LongTensor) tensor,wrappingWrapped));
                            case 7 : return new DoubleD7TensorShell(new LongAsDoubleTensor((LongTensor) tensor,wrappingWrapped));
                            case 8 : return new DoubleD8TensorShell(new LongAsDoubleTensor((LongTensor) tensor,wrappingWrapped));
                            case 9 : return new DoubleD9TensorShell(new LongAsDoubleTensor((LongTensor) tensor,wrappingWrapped));
                            default : return new NumberAsDoubleTensor(tensor,wrappingWrapped);
                        }
                    }
                }
                case FLOAT : {
                    try {
                        switch (tensor.size()) {
                            case 0 : return new FloatAsDoubleD0Tensor((FloatD0Tensor) tensor,wrappingWrapped);
                            case 1 : return new FloatAsDoubleD1Tensor((FloatD1Tensor) tensor,wrappingWrapped);
                            case 2 : return new FloatAsDoubleD2Tensor((FloatD2Tensor) tensor,wrappingWrapped);
                            case 3 : return new FloatAsDoubleD3Tensor((FloatD3Tensor) tensor,wrappingWrapped);
                            case 4 : return new FloatAsDoubleD4Tensor((FloatD4Tensor) tensor,wrappingWrapped);
                            case 5 : return new FloatAsDoubleD5Tensor((FloatD5Tensor) tensor,wrappingWrapped);
                            case 6 : return new FloatAsDoubleD6Tensor((FloatD6Tensor) tensor,wrappingWrapped);
                            case 7 : return new FloatAsDoubleD7Tensor((FloatD7Tensor) tensor,wrappingWrapped);
                            case 8 : return new FloatAsDoubleD8Tensor((FloatD8Tensor) tensor,wrappingWrapped);
                            case 9 : return new FloatAsDoubleD9Tensor((FloatD9Tensor) tensor,wrappingWrapped);
                            default : return new FloatAsDoubleTensor((FloatTensor) tensor,wrappingWrapped);
                        }
                    } catch (ClassCastException e) {
                        //not explicitly sized
                        switch (tensor.size()) {
                            case 0 : return new DoubleD0TensorShell(new FloatAsDoubleTensor((FloatTensor) tensor,wrappingWrapped));
                            case 1 : return new DoubleD1TensorShell(new FloatAsDoubleTensor((FloatTensor) tensor,wrappingWrapped));
                            case 2 : return new DoubleD2TensorShell(new FloatAsDoubleTensor((FloatTensor) tensor,wrappingWrapped));
                            case 3 : return new DoubleD3TensorShell(new FloatAsDoubleTensor((FloatTensor) tensor,wrappingWrapped));
                            case 4 : return new DoubleD4TensorShell(new FloatAsDoubleTensor((FloatTensor) tensor,wrappingWrapped));
                            case 5 : return new DoubleD5TensorShell(new FloatAsDoubleTensor((FloatTensor) tensor,wrappingWrapped));
                            case 6 : return new DoubleD6TensorShell(new FloatAsDoubleTensor((FloatTensor) tensor,wrappingWrapped));
                            case 7 : return new DoubleD7TensorShell(new FloatAsDoubleTensor((FloatTensor) tensor,wrappingWrapped));
                            case 8 : return new DoubleD8TensorShell(new FloatAsDoubleTensor((FloatTensor) tensor,wrappingWrapped));
                            case 9 : return new DoubleD9TensorShell(new FloatAsDoubleTensor((FloatTensor) tensor,wrappingWrapped));
                            default : return new NumberAsDoubleTensor(tensor,wrappingWrapped);
                        }
                    }
                }
                case DOUBLE : {
                    try {
                        return (DoubleTensor) tensor;
                    } catch (ClassCastException e) {
                        return new NumberAsDoubleTensor(tensor);
                    }
                }
                default : throw new IllegalArgumentException("Invalid tensor JavaType for as[Number] method: " + tensor.getType());
            }
        } catch (ClassCastException e) {
            switch (tensor.size()) {
                case 0 : return new DoubleD0TensorShell(new NumberAsDoubleTensor(tensor,wrappingWrapped));
                case 1 : return new DoubleD1TensorShell(new NumberAsDoubleTensor(tensor,wrappingWrapped));
                case 2 : return new DoubleD2TensorShell(new NumberAsDoubleTensor(tensor,wrappingWrapped));
                case 3 : return new DoubleD3TensorShell(new NumberAsDoubleTensor(tensor,wrappingWrapped));
                case 4 : return new DoubleD4TensorShell(new NumberAsDoubleTensor(tensor,wrappingWrapped));
                case 5 : return new DoubleD5TensorShell(new NumberAsDoubleTensor(tensor,wrappingWrapped));
                case 6 : return new DoubleD6TensorShell(new NumberAsDoubleTensor(tensor,wrappingWrapped));
                case 7 : return new DoubleD7TensorShell(new NumberAsDoubleTensor(tensor,wrappingWrapped));
                case 8 : return new DoubleD8TensorShell(new NumberAsDoubleTensor(tensor,wrappingWrapped));
                case 9 : return new DoubleD9TensorShell(new NumberAsDoubleTensor(tensor,wrappingWrapped));
                default : return new NumberAsDoubleTensor(tensor,wrappingWrapped);
            }
        }
    }
    
    static FloatTensor asFloatTensor(Tensor<? extends Number> tensor) {
        switch(tensor.size()) {
            case 0 : return new DoubleAsFloatD0Tensor((DoubleD0Tensor) asDoubleTensor(tensor),true);
            case 1 : return new DoubleAsFloatD1Tensor((DoubleD1Tensor) asDoubleTensor(tensor),true);
            case 2 : return new DoubleAsFloatD2Tensor((DoubleD2Tensor) asDoubleTensor(tensor),true);
            case 3 : return new DoubleAsFloatD3Tensor((DoubleD3Tensor) asDoubleTensor(tensor),true);
            case 4 : return new DoubleAsFloatD4Tensor((DoubleD4Tensor) asDoubleTensor(tensor),true);
            case 5 : return new DoubleAsFloatD5Tensor((DoubleD5Tensor) asDoubleTensor(tensor),true);
            case 6 : return new DoubleAsFloatD6Tensor((DoubleD6Tensor) asDoubleTensor(tensor),true);
            case 7 : return new DoubleAsFloatD7Tensor((DoubleD7Tensor) asDoubleTensor(tensor),true);
            case 8 : return new DoubleAsFloatD8Tensor((DoubleD8Tensor) asDoubleTensor(tensor),true);
            case 9 : return new DoubleAsFloatD9Tensor((DoubleD9Tensor) asDoubleTensor(tensor),true);
            default : return new DoubleAsFloatTensor(asDoubleTensor(tensor),true);
        }
    }

    static LongTensor asLongTensor(Tensor<? extends Number> tensor) {
        return asLongTensor(tensor,false);
    }

    static LongTensor asLongTensor(Tensor<? extends Number> tensor, boolean wrappingWrapped) {
        try {
            switch (tensor.getType()) {
                case DOUBLE : {
                    try {
                        switch (tensor.size()) {
                            case 0 : return new DoubleAsLongD0Tensor((DoubleD0Tensor) tensor,wrappingWrapped);
                            case 1 : return new DoubleAsLongD1Tensor((DoubleD1Tensor) tensor,wrappingWrapped);
                            case 2 : return new DoubleAsLongD2Tensor((DoubleD2Tensor) tensor,wrappingWrapped);
                            case 3 : return new DoubleAsLongD3Tensor((DoubleD3Tensor) tensor,wrappingWrapped);
                            case 4 : return new DoubleAsLongD4Tensor((DoubleD4Tensor) tensor,wrappingWrapped);
                            case 5 : return new DoubleAsLongD5Tensor((DoubleD5Tensor) tensor,wrappingWrapped);
                            case 6 : return new DoubleAsLongD6Tensor((DoubleD6Tensor) tensor,wrappingWrapped);
                            case 7 : return new DoubleAsLongD7Tensor((DoubleD7Tensor) tensor,wrappingWrapped);
                            case 8 : return new DoubleAsLongD8Tensor((DoubleD8Tensor) tensor,wrappingWrapped);
                            case 9 : return new DoubleAsLongD9Tensor((DoubleD9Tensor) tensor,wrappingWrapped);
                            default : return new DoubleAsLongTensor((DoubleTensor) tensor,wrappingWrapped);
                        }
                    } catch (ClassCastException e) {
                        //not explicitly sized
                        switch (tensor.size()) {
                            case 0 : return new LongD0TensorShell(new DoubleAsLongTensor((DoubleTensor) tensor,wrappingWrapped));
                            case 1 : return new LongD1TensorShell(new DoubleAsLongTensor((DoubleTensor) tensor,wrappingWrapped));
                            case 2 : return new LongD2TensorShell(new DoubleAsLongTensor((DoubleTensor) tensor,wrappingWrapped));
                            case 3 : return new LongD3TensorShell(new DoubleAsLongTensor((DoubleTensor) tensor,wrappingWrapped));
                            case 4 : return new LongD4TensorShell(new DoubleAsLongTensor((DoubleTensor) tensor,wrappingWrapped));
                            case 5 : return new LongD5TensorShell(new DoubleAsLongTensor((DoubleTensor) tensor,wrappingWrapped));
                            case 6 : return new LongD6TensorShell(new DoubleAsLongTensor((DoubleTensor) tensor,wrappingWrapped));
                            case 7 : return new LongD7TensorShell(new DoubleAsLongTensor((DoubleTensor) tensor,wrappingWrapped));
                            case 8 : return new LongD8TensorShell(new DoubleAsLongTensor((DoubleTensor) tensor,wrappingWrapped));
                            case 9 : return new LongD9TensorShell(new DoubleAsLongTensor((DoubleTensor) tensor,wrappingWrapped));
                            default : return new NumberAsLongTensor(tensor,wrappingWrapped);
                        }
                    }
                }
                case FLOAT : return asLongTensor(asDoubleTensor(tensor),true);
                case INT : {
                    try {
                        switch (tensor.size()) {
                            case 0 : return new IntAsLongD0Tensor((IntD0Tensor) tensor,wrappingWrapped);
                            case 1 : return new IntAsLongD1Tensor((IntD1Tensor) tensor,wrappingWrapped);
                            case 2 : return new IntAsLongD2Tensor((IntD2Tensor) tensor,wrappingWrapped);
                            case 3 : return new IntAsLongD3Tensor((IntD3Tensor) tensor,wrappingWrapped);
                            case 4 : return new IntAsLongD4Tensor((IntD4Tensor) tensor,wrappingWrapped);
                            case 5 : return new IntAsLongD5Tensor((IntD5Tensor) tensor,wrappingWrapped);
                            case 6 : return new IntAsLongD6Tensor((IntD6Tensor) tensor,wrappingWrapped);
                            case 7 : return new IntAsLongD7Tensor((IntD7Tensor) tensor,wrappingWrapped);
                            case 8 : return new IntAsLongD8Tensor((IntD8Tensor) tensor,wrappingWrapped);
                            case 9 : return new IntAsLongD9Tensor((IntD9Tensor) tensor,wrappingWrapped);
                            default : return new IntAsLongTensor((IntTensor) tensor,wrappingWrapped);
                        }
                    } catch (ClassCastException e) {
                        //not explicitly sized
                        switch (tensor.size()) {
                            case 0 : return new LongD0TensorShell(new IntAsLongTensor((IntTensor) tensor,wrappingWrapped));
                            case 1 : return new LongD1TensorShell(new IntAsLongTensor((IntTensor) tensor,wrappingWrapped));
                            case 2 : return new LongD2TensorShell(new IntAsLongTensor((IntTensor) tensor,wrappingWrapped));
                            case 3 : return new LongD3TensorShell(new IntAsLongTensor((IntTensor) tensor,wrappingWrapped));
                            case 4 : return new LongD4TensorShell(new IntAsLongTensor((IntTensor) tensor,wrappingWrapped));
                            case 5 : return new LongD5TensorShell(new IntAsLongTensor((IntTensor) tensor,wrappingWrapped));
                            case 6 : return new LongD6TensorShell(new IntAsLongTensor((IntTensor) tensor,wrappingWrapped));
                            case 7 : return new LongD7TensorShell(new IntAsLongTensor((IntTensor) tensor,wrappingWrapped));
                            case 8 : return new LongD8TensorShell(new IntAsLongTensor((IntTensor) tensor,wrappingWrapped));
                            case 9 : return new LongD9TensorShell(new IntAsLongTensor((IntTensor) tensor,wrappingWrapped));
                            default : return new NumberAsLongTensor(tensor,wrappingWrapped);
                        }
                    }
                }
                case SHORT : {
                    try {
                        switch (tensor.size()) {
                            case 0 : return new ShortAsLongD0Tensor((ShortD0Tensor) tensor,wrappingWrapped);
                            case 1 : return new ShortAsLongD1Tensor((ShortD1Tensor) tensor,wrappingWrapped);
                            case 2 : return new ShortAsLongD2Tensor((ShortD2Tensor) tensor,wrappingWrapped);
                            case 3 : return new ShortAsLongD3Tensor((ShortD3Tensor) tensor,wrappingWrapped);
                            case 4 : return new ShortAsLongD4Tensor((ShortD4Tensor) tensor,wrappingWrapped);
                            case 5 : return new ShortAsLongD5Tensor((ShortD5Tensor) tensor,wrappingWrapped);
                            case 6 : return new ShortAsLongD6Tensor((ShortD6Tensor) tensor,wrappingWrapped);
                            case 7 : return new ShortAsLongD7Tensor((ShortD7Tensor) tensor,wrappingWrapped);
                            case 8 : return new ShortAsLongD8Tensor((ShortD8Tensor) tensor,wrappingWrapped);
                            case 9 : return new ShortAsLongD9Tensor((ShortD9Tensor) tensor,wrappingWrapped);
                            default : return new ShortAsLongTensor((ShortTensor) tensor,wrappingWrapped);
                        }
                    } catch (ClassCastException e) {
                        //not explicitly sized
                        switch (tensor.size()) {
                            case 0 : return new LongD0TensorShell(new ShortAsLongTensor((ShortTensor) tensor,wrappingWrapped));
                            case 1 : return new LongD1TensorShell(new ShortAsLongTensor((ShortTensor) tensor,wrappingWrapped));
                            case 2 : return new LongD2TensorShell(new ShortAsLongTensor((ShortTensor) tensor,wrappingWrapped));
                            case 3 : return new LongD3TensorShell(new ShortAsLongTensor((ShortTensor) tensor,wrappingWrapped));
                            case 4 : return new LongD4TensorShell(new ShortAsLongTensor((ShortTensor) tensor,wrappingWrapped));
                            case 5 : return new LongD5TensorShell(new ShortAsLongTensor((ShortTensor) tensor,wrappingWrapped));
                            case 6 : return new LongD6TensorShell(new ShortAsLongTensor((ShortTensor) tensor,wrappingWrapped));
                            case 7 : return new LongD7TensorShell(new ShortAsLongTensor((ShortTensor) tensor,wrappingWrapped));
                            case 8 : return new LongD8TensorShell(new ShortAsLongTensor((ShortTensor) tensor,wrappingWrapped));
                            case 9 : return new LongD9TensorShell(new ShortAsLongTensor((ShortTensor) tensor,wrappingWrapped));
                            default : return new NumberAsLongTensor(tensor,wrappingWrapped);
                        }
                    }
                }
                case BYTE : {
                    try {
                        switch (tensor.size()) {
                            case 0 : return new ByteAsLongD0Tensor((ByteD0Tensor) tensor,wrappingWrapped);
                            case 1 : return new ByteAsLongD1Tensor((ByteD1Tensor) tensor,wrappingWrapped);
                            case 2 : return new ByteAsLongD2Tensor((ByteD2Tensor) tensor,wrappingWrapped);
                            case 3 : return new ByteAsLongD3Tensor((ByteD3Tensor) tensor,wrappingWrapped);
                            case 4 : return new ByteAsLongD4Tensor((ByteD4Tensor) tensor,wrappingWrapped);
                            case 5 : return new ByteAsLongD5Tensor((ByteD5Tensor) tensor,wrappingWrapped);
                            case 6 : return new ByteAsLongD6Tensor((ByteD6Tensor) tensor,wrappingWrapped);
                            case 7 : return new ByteAsLongD7Tensor((ByteD7Tensor) tensor,wrappingWrapped);
                            case 8 : return new ByteAsLongD8Tensor((ByteD8Tensor) tensor,wrappingWrapped);
                            case 9 : return new ByteAsLongD9Tensor((ByteD9Tensor) tensor,wrappingWrapped);
                            default : return new ByteAsLongTensor((ByteTensor) tensor,wrappingWrapped);
                        }
                    } catch (ClassCastException e) {
                        //not explicitly sized
                        switch (tensor.size()) {
                            case 0 : return new LongD0TensorShell(new ByteAsLongTensor((ByteTensor) tensor,wrappingWrapped));
                            case 1 : return new LongD1TensorShell(new ByteAsLongTensor((ByteTensor) tensor,wrappingWrapped));
                            case 2 : return new LongD2TensorShell(new ByteAsLongTensor((ByteTensor) tensor,wrappingWrapped));
                            case 3 : return new LongD3TensorShell(new ByteAsLongTensor((ByteTensor) tensor,wrappingWrapped));
                            case 4 : return new LongD4TensorShell(new ByteAsLongTensor((ByteTensor) tensor,wrappingWrapped));
                            case 5 : return new LongD5TensorShell(new ByteAsLongTensor((ByteTensor) tensor,wrappingWrapped));
                            case 6 : return new LongD6TensorShell(new ByteAsLongTensor((ByteTensor) tensor,wrappingWrapped));
                            case 7 : return new LongD7TensorShell(new ByteAsLongTensor((ByteTensor) tensor,wrappingWrapped));
                            case 8 : return new LongD8TensorShell(new ByteAsLongTensor((ByteTensor) tensor,wrappingWrapped));
                            case 9 : return new LongD9TensorShell(new ByteAsLongTensor((ByteTensor) tensor,wrappingWrapped));
                            default : return new NumberAsLongTensor(tensor,wrappingWrapped);
                        }
                    }
                }
                case LONG : {
                    try {
                        return (LongTensor) tensor;
                    } catch (ClassCastException e) {
                        return new NumberAsLongTensor(tensor);
                    }
                }
                default : throw new IllegalArgumentException("Invalid tensor JavaType for as[Number] method: " + tensor.getType());
            }
        } catch (ClassCastException e) {
            switch (tensor.size()) {
                case 0 : return new LongD0TensorShell(new NumberAsLongTensor(tensor,wrappingWrapped));
                case 1 : return new LongD1TensorShell(new NumberAsLongTensor(tensor,wrappingWrapped));
                case 2 : return new LongD2TensorShell(new NumberAsLongTensor(tensor,wrappingWrapped));
                case 3 : return new LongD3TensorShell(new NumberAsLongTensor(tensor,wrappingWrapped));
                case 4 : return new LongD4TensorShell(new NumberAsLongTensor(tensor,wrappingWrapped));
                case 5 : return new LongD5TensorShell(new NumberAsLongTensor(tensor,wrappingWrapped));
                case 6 : return new LongD6TensorShell(new NumberAsLongTensor(tensor,wrappingWrapped));
                case 7 : return new LongD7TensorShell(new NumberAsLongTensor(tensor,wrappingWrapped));
                case 8 : return new LongD8TensorShell(new NumberAsLongTensor(tensor,wrappingWrapped));
                case 9 : return new LongD9TensorShell(new NumberAsLongTensor(tensor,wrappingWrapped));
                default : return new NumberAsLongTensor(tensor,wrappingWrapped);
            }
        }
    }
    
    static IntTensor asIntTensor(Tensor<? extends Number> tensor) {
        switch(tensor.size()) {
            case 0 : return new LongAsIntD0Tensor((LongD0Tensor) asLongTensor(tensor),true);
            case 1 : return new LongAsIntD1Tensor((LongD1Tensor) asLongTensor(tensor),true);
            case 2 : return new LongAsIntD2Tensor((LongD2Tensor) asLongTensor(tensor),true);
            case 3 : return new LongAsIntD3Tensor((LongD3Tensor) asLongTensor(tensor),true);
            case 4 : return new LongAsIntD4Tensor((LongD4Tensor) asLongTensor(tensor),true);
            case 5 : return new LongAsIntD5Tensor((LongD5Tensor) asLongTensor(tensor),true);
            case 6 : return new LongAsIntD6Tensor((LongD6Tensor) asLongTensor(tensor),true);
            case 7 : return new LongAsIntD7Tensor((LongD7Tensor) asLongTensor(tensor),true);
            case 8 : return new LongAsIntD8Tensor((LongD8Tensor) asLongTensor(tensor),true);
            case 9 : return new LongAsIntD9Tensor((LongD9Tensor) asLongTensor(tensor),true);
            default : return new LongAsIntTensor(asLongTensor(tensor),true);
        }
    }      
    
    static ShortTensor asShortTensor(Tensor<? extends Number> tensor) {
        switch(tensor.size()) {
            case 0 : return new LongAsShortD0Tensor((LongD0Tensor) asLongTensor(tensor),true);
            case 1 : return new LongAsShortD1Tensor((LongD1Tensor) asLongTensor(tensor),true);
            case 2 : return new LongAsShortD2Tensor((LongD2Tensor) asLongTensor(tensor),true);
            case 3 : return new LongAsShortD3Tensor((LongD3Tensor) asLongTensor(tensor),true);
            case 4 : return new LongAsShortD4Tensor((LongD4Tensor) asLongTensor(tensor),true);
            case 5 : return new LongAsShortD5Tensor((LongD5Tensor) asLongTensor(tensor),true);
            case 6 : return new LongAsShortD6Tensor((LongD6Tensor) asLongTensor(tensor),true);
            case 7 : return new LongAsShortD7Tensor((LongD7Tensor) asLongTensor(tensor),true);
            case 8 : return new LongAsShortD8Tensor((LongD8Tensor) asLongTensor(tensor),true);
            case 9 : return new LongAsShortD9Tensor((LongD9Tensor) asLongTensor(tensor),true);
            default : return new LongAsShortTensor(asLongTensor(tensor),true);
        }
    }   
    
    static ByteTensor asByteTensor(Tensor<? extends Number> tensor) {
        switch(tensor.size()) {
            case 0 : return new LongAsByteD0Tensor((LongD0Tensor) asLongTensor(tensor),true);
            case 1 : return new LongAsByteD1Tensor((LongD1Tensor) asLongTensor(tensor),true);
            case 2 : return new LongAsByteD2Tensor((LongD2Tensor) asLongTensor(tensor),true);
            case 3 : return new LongAsByteD3Tensor((LongD3Tensor) asLongTensor(tensor),true);
            case 4 : return new LongAsByteD4Tensor((LongD4Tensor) asLongTensor(tensor),true);
            case 5 : return new LongAsByteD5Tensor((LongD5Tensor) asLongTensor(tensor),true);
            case 6 : return new LongAsByteD6Tensor((LongD6Tensor) asLongTensor(tensor),true);
            case 7 : return new LongAsByteD7Tensor((LongD7Tensor) asLongTensor(tensor),true);
            case 8 : return new LongAsByteD8Tensor((LongD8Tensor) asLongTensor(tensor),true);
            case 9 : return new LongAsByteD9Tensor((LongD9Tensor) asLongTensor(tensor),true);
            default : return new LongAsByteTensor(asLongTensor(tensor),true);
        }
    }

    ///////////////////object casting///////////////////////////////
    private static class NumberAsDoubleTensor extends AbstractDoubleTensor implements WrappedTensor {
        private final Tensor<? extends Number> tensor;
        private final JavaType type;
        private final boolean wrappingWrapped;

        private NumberAsDoubleTensor(Tensor<? extends Number> tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            type = tensor.getType();
            this.wrappingWrapped = wrappingWrapped;
        }

        private NumberAsDoubleTensor(Tensor<? extends Number> tensor) {
            this(tensor,false);
        }

        @Override
        public double getCell(int ... indices) {
            return tensor.getValue(indices).doubleValue();
        }

        @Override
        @SuppressWarnings("unchecked") //ok because of JavaType requirements
        public void setCell(double value, int ... indices) {
            switch(type) {                                                            
                case DOUBLE : ((Tensor<Double>) tensor).setValue(value,indices); break;
                case FLOAT : ((Tensor<Float>) tensor).setValue((float) value,indices); break;
                case LONG : ((Tensor<Long>) tensor).setValue(Math.round(value),indices); break;
                case INT : ((Tensor<Integer>) tensor).setValue((int) Math.round(value),indices); break;
                case SHORT : ((Tensor<Short>) tensor).setValue((short) Math.round(value),indices); break;
                case BYTE : ((Tensor<Byte>) tensor).setValue((byte) Math.round(value),indices); break;
            }
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }     

    private static class NumberAsLongTensor extends AbstractLongTensor implements WrappedTensor {
        private final Tensor<? extends Number> tensor;
        private final JavaType type;
        private final boolean wrappingWrapped;

        private NumberAsLongTensor(Tensor<? extends Number> tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            type = tensor.getType();
            this.wrappingWrapped = wrappingWrapped;
        }

        private NumberAsLongTensor(Tensor<? extends Number> tensor) {
            this(tensor,false);
        }

        @Override         
        @SuppressWarnings("unchecked") //ok because of JavaType requirements
        public long getCell(int ... indices) {
            switch(type) {
                case DOUBLE : return Math.round(((Tensor<Double>) tensor).getValue());
                case FLOAT : return Math.round(((Tensor<Float>) tensor).getValue());
                default : return tensor.getValue(indices).longValue();                
            }
        }

        @Override              
        @SuppressWarnings("unchecked") //ok because of JavaType requirements
        public void setCell(long value, int ... indices) {
            switch(type) {
                case DOUBLE : ((Tensor<Double>) tensor).setValue((double) value,indices); break;
                case FLOAT : ((Tensor<Float>) tensor).setValue((float) value,indices); break;
                case LONG : ((Tensor<Long>) tensor).setValue(value,indices); break;
                case INT : ((Tensor<Integer>) tensor).setValue((int) value,indices); break;
                case SHORT : ((Tensor<Short>) tensor).setValue((short) value,indices); break;
                case BYTE : ((Tensor<Byte>) tensor).setValue((byte) value,indices); break;
            }
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    } 

    //////////////////upcasting////////////////////////////////////

    private static class ByteAsLongD0Tensor extends LongD0TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ByteD0Tensor tensor;

        private ByteAsLongD0Tensor(ByteD0Tensor tensor, boolean wrappingWrapped) {
            super(new ByteAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell() {
            return tensor.getCell();
        }

        @Override
        public void setCell(long value) {
            tensor.setCell((byte) value);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ByteAsLongD1Tensor extends LongD1TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ByteD1Tensor tensor;

        private ByteAsLongD1Tensor(ByteD1Tensor tensor, boolean wrappingWrapped) {
            super(new ByteAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index) {
            return tensor.getCell(d0index);
        }

        @Override
        public void setCell(long value, int d0index) {
            tensor.setCell((byte) value,d0index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ByteAsLongD2Tensor extends LongD2TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ByteD2Tensor tensor;

        private ByteAsLongD2Tensor(ByteD2Tensor tensor, boolean wrappingWrapped) {
            super(new ByteAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index) {
            return tensor.getCell(d0index,d1index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index) {
            tensor.setCell((byte) value,d0index,d1index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ByteAsLongD3Tensor extends LongD3TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ByteD3Tensor tensor;

        private ByteAsLongD3Tensor(ByteD3Tensor tensor, boolean wrappingWrapped) {
            super(new ByteAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index) {
            return tensor.getCell(d0index,d1index,d2index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index) {
            tensor.setCell((byte) value,d0index,d1index,d2index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ByteAsLongD4Tensor extends LongD4TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ByteD4Tensor tensor;

        private ByteAsLongD4Tensor(ByteD4Tensor tensor, boolean wrappingWrapped) {
            super(new ByteAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.getCell(d0index,d1index,d2index,d3index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index) {
            tensor.setCell((byte) value,d0index,d1index,d2index,d3index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ByteAsLongD5Tensor extends LongD5TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ByteD5Tensor tensor;

        private ByteAsLongD5Tensor(ByteD5Tensor tensor, boolean wrappingWrapped) {
            super(new ByteAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensor.setCell((byte) value,d0index,d1index,d2index,d3index,d4index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ByteAsLongD6Tensor extends LongD6TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ByteD6Tensor tensor;

        private ByteAsLongD6Tensor(ByteD6Tensor tensor, boolean wrappingWrapped) {
            super(new ByteAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensor.setCell((byte) value,d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ByteAsLongD7Tensor extends LongD7TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ByteD7Tensor tensor;

        private ByteAsLongD7Tensor(ByteD7Tensor tensor, boolean wrappingWrapped) {
            super(new ByteAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensor.setCell((byte) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ByteAsLongD8Tensor extends LongD8TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ByteD8Tensor tensor;

        private ByteAsLongD8Tensor(ByteD8Tensor tensor, boolean wrappingWrapped) {
            super(new ByteAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensor.setCell((byte) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ByteAsLongD9Tensor extends LongD9TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ByteD9Tensor tensor;

        private ByteAsLongD9Tensor(ByteD9Tensor tensor, boolean wrappingWrapped) {
            super(new ByteAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensor.setCell((byte) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ByteAsLongTensor extends AbstractLongTensor implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ByteTensor tensor;

        public ByteAsLongTensor(ByteTensor tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        private ByteAsLongTensor (ByteTensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int ... indices) {
            return (long) tensor.getCell(indices);
        }

        @Override
        public void setCell(long value, int ... indices) {
            tensor.setCell((byte) value,indices);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ShortAsLongD0Tensor extends LongD0TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ShortD0Tensor tensor;

        private ShortAsLongD0Tensor(ShortD0Tensor tensor, boolean wrappingWrapped) {
            super(new ShortAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell() {
            return tensor.getCell();
        }

        @Override
        public void setCell(long value) {
            tensor.setCell((short) value);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ShortAsLongD1Tensor extends LongD1TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ShortD1Tensor tensor;

        private ShortAsLongD1Tensor(ShortD1Tensor tensor, boolean wrappingWrapped) {
            super(new ShortAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index) {
            return tensor.getCell(d0index);
        }

        @Override
        public void setCell(long value, int d0index) {
            tensor.setCell((short) value,d0index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ShortAsLongD2Tensor extends LongD2TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ShortD2Tensor tensor;

        private ShortAsLongD2Tensor(ShortD2Tensor tensor, boolean wrappingWrapped) {
            super(new ShortAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index) {
            return tensor.getCell(d0index,d1index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index) {
            tensor.setCell((short) value,d0index,d1index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ShortAsLongD3Tensor extends LongD3TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ShortD3Tensor tensor;

        private ShortAsLongD3Tensor(ShortD3Tensor tensor, boolean wrappingWrapped) {
            super(new ShortAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index) {
            return tensor.getCell(d0index,d1index,d2index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index) {
            tensor.setCell((short) value,d0index,d1index,d2index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ShortAsLongD4Tensor extends LongD4TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ShortD4Tensor tensor;

        private ShortAsLongD4Tensor(ShortD4Tensor tensor, boolean wrappingWrapped) {
            super(new ShortAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.getCell(d0index,d1index,d2index,d3index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index) {
            tensor.setCell((short) value,d0index,d1index,d2index,d3index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ShortAsLongD5Tensor extends LongD5TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ShortD5Tensor tensor;

        private ShortAsLongD5Tensor(ShortD5Tensor tensor, boolean wrappingWrapped) {
            super(new ShortAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensor.setCell((short) value,d0index,d1index,d2index,d3index,d4index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ShortAsLongD6Tensor extends LongD6TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ShortD6Tensor tensor;

        private ShortAsLongD6Tensor(ShortD6Tensor tensor, boolean wrappingWrapped) {
            super(new ShortAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensor.setCell((short) value,d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ShortAsLongD7Tensor extends LongD7TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ShortD7Tensor tensor;

        private ShortAsLongD7Tensor(ShortD7Tensor tensor, boolean wrappingWrapped) {
            super(new ShortAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensor.setCell((short) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ShortAsLongD8Tensor extends LongD8TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ShortD8Tensor tensor;

        private ShortAsLongD8Tensor(ShortD8Tensor tensor, boolean wrappingWrapped) {
            super(new ShortAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensor.setCell((short) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ShortAsLongD9Tensor extends LongD9TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ShortD9Tensor tensor;

        private ShortAsLongD9Tensor(ShortD9Tensor tensor, boolean wrappingWrapped) {
            super(new ShortAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensor.setCell((short) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class ShortAsLongTensor extends AbstractLongTensor implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final ShortTensor tensor;

        public ShortAsLongTensor(ShortTensor tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        private ShortAsLongTensor (ShortTensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int ... indices) {
            return (long) tensor.getCell(indices);
        }

        @Override
        public void setCell(long value, int ... indices) {
            tensor.setCell((short) value,indices);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class IntAsLongD0Tensor extends LongD0TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final IntD0Tensor tensor;

        private IntAsLongD0Tensor(IntD0Tensor tensor, boolean wrappingWrapped) {
            super(new IntAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell() {
            return tensor.getCell();
        }

        @Override
        public void setCell(long value) {
            tensor.setCell((int) value);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class IntAsLongD1Tensor extends LongD1TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final IntD1Tensor tensor;

        private IntAsLongD1Tensor(IntD1Tensor tensor, boolean wrappingWrapped) {
            super(new IntAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index) {
            return tensor.getCell(d0index);
        }

        @Override
        public void setCell(long value, int d0index) {
            tensor.setCell((int) value,d0index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class IntAsLongD2Tensor extends LongD2TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final IntD2Tensor tensor;

        private IntAsLongD2Tensor(IntD2Tensor tensor, boolean wrappingWrapped) {
            super(new IntAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index) {
            return tensor.getCell(d0index,d1index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index) {
            tensor.setCell((int) value,d0index,d1index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class IntAsLongD3Tensor extends LongD3TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final IntD3Tensor tensor;

        private IntAsLongD3Tensor(IntD3Tensor tensor, boolean wrappingWrapped) {
            super(new IntAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index) {
            return tensor.getCell(d0index,d1index,d2index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index) {
            tensor.setCell((int) value,d0index,d1index,d2index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class IntAsLongD4Tensor extends LongD4TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final IntD4Tensor tensor;

        private IntAsLongD4Tensor(IntD4Tensor tensor, boolean wrappingWrapped) {
            super(new IntAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.getCell(d0index,d1index,d2index,d3index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index) {
            tensor.setCell((int) value,d0index,d1index,d2index,d3index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class IntAsLongD5Tensor extends LongD5TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final IntD5Tensor tensor;

        private IntAsLongD5Tensor(IntD5Tensor tensor, boolean wrappingWrapped) {
            super(new IntAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensor.setCell((int) value,d0index,d1index,d2index,d3index,d4index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class IntAsLongD6Tensor extends LongD6TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final IntD6Tensor tensor;

        private IntAsLongD6Tensor(IntD6Tensor tensor, boolean wrappingWrapped) {
            super(new IntAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensor.setCell((int) value,d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class IntAsLongD7Tensor extends LongD7TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final IntD7Tensor tensor;

        private IntAsLongD7Tensor(IntD7Tensor tensor, boolean wrappingWrapped) {
            super(new IntAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensor.setCell((int) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class IntAsLongD8Tensor extends LongD8TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final IntD8Tensor tensor;

        private IntAsLongD8Tensor(IntD8Tensor tensor, boolean wrappingWrapped) {
            super(new IntAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensor.setCell((int) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class IntAsLongD9Tensor extends LongD9TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final IntD9Tensor tensor;

        private IntAsLongD9Tensor(IntD9Tensor tensor, boolean wrappingWrapped) {
            super(new IntAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensor.setCell((int) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class IntAsLongTensor extends AbstractLongTensor implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final IntTensor tensor;

        public IntAsLongTensor(IntTensor tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        private IntAsLongTensor (IntTensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int ... indices) {
            return (long) tensor.getCell(indices);
        }

        @Override
        public void setCell(long value, int ... indices) {
            tensor.setCell((int) value,indices);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class LongAsDoubleD0Tensor extends DoubleD0TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final LongD0Tensor tensor;

        private LongAsDoubleD0Tensor(LongD0Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell() {
            return tensor.getCell();
        }

        @Override
        public void setCell(double value) {
            tensor.setCell(Math.round(value));
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class LongAsDoubleD1Tensor extends DoubleD1TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final LongD1Tensor tensor;

        private LongAsDoubleD1Tensor(LongD1Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index) {
            return tensor.getCell(d0index);
        }

        @Override
        public void setCell(double value, int d0index) {
            tensor.setCell(Math.round(value),d0index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class LongAsDoubleD2Tensor extends DoubleD2TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final LongD2Tensor tensor;

        private LongAsDoubleD2Tensor(LongD2Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index) {
            return tensor.getCell(d0index,d1index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index) {
            tensor.setCell(Math.round(value),d0index,d1index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class LongAsDoubleD3Tensor extends DoubleD3TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final LongD3Tensor tensor;

        private LongAsDoubleD3Tensor(LongD3Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index) {
            return tensor.getCell(d0index,d1index,d2index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index) {
            tensor.setCell(Math.round(value),d0index,d1index,d2index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class LongAsDoubleD4Tensor extends DoubleD4TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final LongD4Tensor tensor;

        private LongAsDoubleD4Tensor(LongD4Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.getCell(d0index,d1index,d2index,d3index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index) {
            tensor.setCell(Math.round(value),d0index,d1index,d2index,d3index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class LongAsDoubleD5Tensor extends DoubleD5TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final LongD5Tensor tensor;

        private LongAsDoubleD5Tensor(LongD5Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensor.setCell(Math.round(value),d0index,d1index,d2index,d3index,d4index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class LongAsDoubleD6Tensor extends DoubleD6TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final LongD6Tensor tensor;

        private LongAsDoubleD6Tensor(LongD6Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensor.setCell(Math.round(value),d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class LongAsDoubleD7Tensor extends DoubleD7TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final LongD7Tensor tensor;

        private LongAsDoubleD7Tensor(LongD7Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensor.setCell(Math.round(value),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class LongAsDoubleD8Tensor extends DoubleD8TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final LongD8Tensor tensor;

        private LongAsDoubleD8Tensor(LongD8Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensor.setCell(Math.round(value),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class LongAsDoubleD9Tensor extends DoubleD9TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final LongD9Tensor tensor;

        private LongAsDoubleD9Tensor(LongD9Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensor.setCell(Math.round(value),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class LongAsDoubleTensor extends AbstractDoubleTensor implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final LongTensor tensor;

        public LongAsDoubleTensor(LongTensor tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        private LongAsDoubleTensor (LongTensor tensor) {
            this(tensor,false);
        }

        @Override
        public double getCell(int ... indices) {
            return (double) tensor.getCell(indices);
        }

        @Override
        public void setCell(double value, int ... indices) {
            tensor.setCell(Math.round(value),indices);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class FloatAsDoubleD0Tensor extends DoubleD0TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final FloatD0Tensor tensor;

        private FloatAsDoubleD0Tensor(FloatD0Tensor tensor, boolean wrappingWrapped) {
            super(new FloatAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell() {
            return tensor.getCell();
        }

        @Override
        public void setCell(double value) {
            tensor.setCell((float) value);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class FloatAsDoubleD1Tensor extends DoubleD1TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final FloatD1Tensor tensor;

        private FloatAsDoubleD1Tensor(FloatD1Tensor tensor, boolean wrappingWrapped) {
            super(new FloatAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index) {
            return tensor.getCell(d0index);
        }

        @Override
        public void setCell(double value, int d0index) {
            tensor.setCell((float) value,d0index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class FloatAsDoubleD2Tensor extends DoubleD2TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final FloatD2Tensor tensor;

        private FloatAsDoubleD2Tensor(FloatD2Tensor tensor, boolean wrappingWrapped) {
            super(new FloatAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index) {
            return tensor.getCell(d0index,d1index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index) {
            tensor.setCell((float) value,d0index,d1index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class FloatAsDoubleD3Tensor extends DoubleD3TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final FloatD3Tensor tensor;

        private FloatAsDoubleD3Tensor(FloatD3Tensor tensor, boolean wrappingWrapped) {
            super(new FloatAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index) {
            return tensor.getCell(d0index,d1index,d2index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index) {
            tensor.setCell((float) value,d0index,d1index,d2index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class FloatAsDoubleD4Tensor extends DoubleD4TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final FloatD4Tensor tensor;

        private FloatAsDoubleD4Tensor(FloatD4Tensor tensor, boolean wrappingWrapped) {
            super(new FloatAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.getCell(d0index,d1index,d2index,d3index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index) {
            tensor.setCell((float) value,d0index,d1index,d2index,d3index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class FloatAsDoubleD5Tensor extends DoubleD5TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final FloatD5Tensor tensor;

        private FloatAsDoubleD5Tensor(FloatD5Tensor tensor, boolean wrappingWrapped) {
            super(new FloatAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensor.setCell((float) value,d0index,d1index,d2index,d3index,d4index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class FloatAsDoubleD6Tensor extends DoubleD6TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final FloatD6Tensor tensor;

        private FloatAsDoubleD6Tensor(FloatD6Tensor tensor, boolean wrappingWrapped) {
            super(new FloatAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensor.setCell((float) value,d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class FloatAsDoubleD7Tensor extends DoubleD7TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final FloatD7Tensor tensor;

        private FloatAsDoubleD7Tensor(FloatD7Tensor tensor, boolean wrappingWrapped) {
            super(new FloatAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensor.setCell((float) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class FloatAsDoubleD8Tensor extends DoubleD8TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final FloatD8Tensor tensor;

        private FloatAsDoubleD8Tensor(FloatD8Tensor tensor, boolean wrappingWrapped) {
            super(new FloatAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensor.setCell((float) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class FloatAsDoubleD9Tensor extends DoubleD9TensorShell implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final FloatD9Tensor tensor;

        private FloatAsDoubleD9Tensor(FloatD9Tensor tensor, boolean wrappingWrapped) {
            super(new FloatAsDoubleTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        @Override
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensor.setCell((float) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    private static class FloatAsDoubleTensor extends AbstractDoubleTensor implements WrappedTensor {
        private final boolean wrappingWrapped;
        private final FloatTensor tensor;

        public FloatAsDoubleTensor(FloatTensor tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        private FloatAsDoubleTensor (FloatTensor tensor) {
            this(tensor,false);
        }

        @Override
        public double getCell(int ... indices) {
            return (double) tensor.getCell(indices);
        }

        @Override
        public void setCell(double value, int ... indices) {
            tensor.setCell((float) value,indices);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor(): tensor;
        }
    }

    ////////////////////////////////////downcasting//////////////////////////////

    private static class DoubleAsFloatD0Tensor extends FloatD0TensorShell implements WrappedTensor {
        private final DoubleD0Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsFloatD0Tensor(DoubleD0Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsFloatTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsFloatD0Tensor (DoubleD0Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public float getCell() {
            return (float) tensor.getCell();
        }

        @Override
        public void setCell(float value) {
            tensor.setCell((double) value);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsFloatD1Tensor extends FloatD1TensorShell implements WrappedTensor {
        private final DoubleD1Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsFloatD1Tensor(DoubleD1Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsFloatTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsFloatD1Tensor (DoubleD1Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public float getCell(int d0index) {
            return (float) tensor.getCell(d0index);
        }

        @Override
        public void setCell(float value, int d0index) {
            tensor.setCell((double) value,d0index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsFloatD2Tensor extends FloatD2TensorShell implements WrappedTensor {
        private final DoubleD2Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsFloatD2Tensor(DoubleD2Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsFloatTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsFloatD2Tensor (DoubleD2Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public float getCell(int d0index, int d1index) {
            return (float) tensor.getCell(d0index,d1index);
        }

        @Override
        public void setCell(float value, int d0index, int d1index) {
            tensor.setCell((double) value,d0index,d1index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsFloatD3Tensor extends FloatD3TensorShell implements WrappedTensor {
        private final DoubleD3Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsFloatD3Tensor(DoubleD3Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsFloatTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsFloatD3Tensor (DoubleD3Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public float getCell(int d0index, int d1index, int d2index) {
            return (float) tensor.getCell(d0index,d1index,d2index);
        }

        @Override
        public void setCell(float value, int d0index, int d1index, int d2index) {
            tensor.setCell((double) value,d0index,d1index,d2index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsFloatD4Tensor extends FloatD4TensorShell implements WrappedTensor {
        private final DoubleD4Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsFloatD4Tensor(DoubleD4Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsFloatTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsFloatD4Tensor (DoubleD4Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public float getCell(int d0index, int d1index, int d2index, int d3index) {
            return (float) tensor.getCell(d0index,d1index,d2index,d3index);
        }

        @Override
        public void setCell(float value, int d0index, int d1index, int d2index, int d3index) {
            tensor.setCell((double) value,d0index,d1index,d2index,d3index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsFloatD5Tensor extends FloatD5TensorShell implements WrappedTensor {
        private final DoubleD5Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsFloatD5Tensor(DoubleD5Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsFloatTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsFloatD5Tensor (DoubleD5Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return (float) tensor.getCell(d0index,d1index,d2index,d3index,d4index);
        }

        @Override
        public void setCell(float value, int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensor.setCell((double) value,d0index,d1index,d2index,d3index,d4index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsFloatD6Tensor extends FloatD6TensorShell implements WrappedTensor {
        private final DoubleD6Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsFloatD6Tensor(DoubleD6Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsFloatTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsFloatD6Tensor (DoubleD6Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return (float) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        @Override
        public void setCell(float value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensor.setCell((double) value,d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsFloatD7Tensor extends FloatD7TensorShell implements WrappedTensor {
        private final DoubleD7Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsFloatD7Tensor(DoubleD7Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsFloatTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsFloatD7Tensor (DoubleD7Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return (float) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        @Override
        public void setCell(float value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensor.setCell((double) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsFloatD8Tensor extends FloatD8TensorShell implements WrappedTensor {
        private final DoubleD8Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsFloatD8Tensor(DoubleD8Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsFloatTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsFloatD8Tensor (DoubleD8Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return (float) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        @Override
        public void setCell(float value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensor.setCell((double) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsFloatD9Tensor extends FloatD9TensorShell implements WrappedTensor {
        private final DoubleD9Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsFloatD9Tensor(DoubleD9Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsFloatTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsFloatD9Tensor (DoubleD9Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return (float) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        @Override
        public void setCell(float value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensor.setCell((double) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsFloatTensor extends AbstractFloatTensor  implements WrappedTensor{
        private final DoubleTensor tensor;
        private final boolean wrappingWrapped;

        public DoubleAsFloatTensor(DoubleTensor tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsFloatTensor (DoubleTensor tensor) {
            this(tensor,false);
        }

        @Override
        public float getCell(int ... indices) {
            return (float) tensor.getCell(indices);
        }

        @Override
        public void setCell(float value, int ... indices) {
            tensor.setCell((double) value,indices);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsLongD0Tensor extends LongD0TensorShell implements WrappedTensor {
        private final DoubleD0Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsLongD0Tensor(DoubleD0Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsLongD0Tensor (DoubleD0Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell() {
            return Math.round(tensor.getCell());
        }

        @Override
        public void setCell(long value) {
            tensor.setCell(value);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsLongD1Tensor extends LongD1TensorShell implements WrappedTensor {
        private final DoubleD1Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsLongD1Tensor(DoubleD1Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsLongD1Tensor (DoubleD1Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int d0index) {
            return Math.round(tensor.getCell(d0index));
        }

        @Override
        public void setCell(long value, int d0index) {
            tensor.setCell(value,d0index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsLongD2Tensor extends LongD2TensorShell implements WrappedTensor {
        private final DoubleD2Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsLongD2Tensor(DoubleD2Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsLongD2Tensor (DoubleD2Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int d0index, int d1index) {
            return Math.round(tensor.getCell(d0index,d1index));
        }

        @Override
        public void setCell(long value, int d0index, int d1index) {
            tensor.setCell(value,d0index,d1index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsLongD3Tensor extends LongD3TensorShell implements WrappedTensor {
        private final DoubleD3Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsLongD3Tensor(DoubleD3Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsLongD3Tensor (DoubleD3Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index) {
            return Math.round(tensor.getCell(d0index,d1index,d2index));
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index) {
            tensor.setCell(value,d0index,d1index,d2index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsLongD4Tensor extends LongD4TensorShell implements WrappedTensor {
        private final DoubleD4Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsLongD4Tensor(DoubleD4Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsLongD4Tensor (DoubleD4Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index) {
            return Math.round(tensor.getCell(d0index,d1index,d2index,d3index));
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index) {
            tensor.setCell(value,d0index,d1index,d2index,d3index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsLongD5Tensor extends LongD5TensorShell implements WrappedTensor {
        private final DoubleD5Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsLongD5Tensor(DoubleD5Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsLongD5Tensor (DoubleD5Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return Math.round(tensor.getCell(d0index,d1index,d2index,d3index,d4index));
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensor.setCell(value,d0index,d1index,d2index,d3index,d4index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsLongD6Tensor extends LongD6TensorShell implements WrappedTensor {
        private final DoubleD6Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsLongD6Tensor(DoubleD6Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsLongD6Tensor (DoubleD6Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return Math.round(tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index));
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensor.setCell(value,d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsLongD7Tensor extends LongD7TensorShell implements WrappedTensor {
        private final DoubleD7Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsLongD7Tensor(DoubleD7Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsLongD7Tensor (DoubleD7Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return Math.round(tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index));
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensor.setCell(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsLongD8Tensor extends LongD8TensorShell implements WrappedTensor {
        private final DoubleD8Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsLongD8Tensor(DoubleD8Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsLongD8Tensor (DoubleD8Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return Math.round(tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index));
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensor.setCell(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsLongD9Tensor extends LongD9TensorShell implements WrappedTensor {
        private final DoubleD9Tensor tensor;
        private final boolean wrappingWrapped;

        private DoubleAsLongD9Tensor(DoubleD9Tensor tensor, boolean wrappingWrapped) {
            super(new DoubleAsLongTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsLongD9Tensor (DoubleD9Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return Math.round(tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index));
        }

        @Override
        public void setCell(long value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensor.setCell(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class DoubleAsLongTensor extends AbstractLongTensor implements WrappedTensor {
        private final DoubleTensor tensor;
        private final boolean wrappingWrapped;

        public DoubleAsLongTensor(DoubleTensor tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public DoubleAsLongTensor (DoubleTensor tensor) {
            this(tensor,false);
        }

        @Override
        public long getCell(int ... indices) {
            return Math.round(tensor.getCell(indices));
        }

        @Override
        public void setCell(long value, int ... indices) {
            tensor.setCell(value,indices);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsIntD0Tensor extends IntD0TensorShell implements WrappedTensor {
        private final LongD0Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsIntD0Tensor(LongD0Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsIntTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsIntD0Tensor (LongD0Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public int getCell() {
            return (int) tensor.getCell();
        }

        @Override
        public void setCell(int value) {
            tensor.setCell((long) value);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsIntD1Tensor extends IntD1TensorShell implements WrappedTensor {
        private final LongD1Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsIntD1Tensor(LongD1Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsIntTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsIntD1Tensor (LongD1Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public int getCell(int d0index) {
            return (int) tensor.getCell(d0index);
        }

        @Override
        public void setCell(int value, int d0index) {
            tensor.setCell((long) value,d0index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsIntD2Tensor extends IntD2TensorShell implements WrappedTensor {
        private final LongD2Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsIntD2Tensor(LongD2Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsIntTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsIntD2Tensor (LongD2Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public int getCell(int d0index, int d1index) {
            return (int) tensor.getCell(d0index,d1index);
        }

        @Override
        public void setCell(int value, int d0index, int d1index) {
            tensor.setCell((long) value,d0index,d1index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsIntD3Tensor extends IntD3TensorShell implements WrappedTensor {
        private final LongD3Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsIntD3Tensor(LongD3Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsIntTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsIntD3Tensor (LongD3Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public int getCell(int d0index, int d1index, int d2index) {
            return (int) tensor.getCell(d0index,d1index,d2index);
        }

        @Override
        public void setCell(int value, int d0index, int d1index, int d2index) {
            tensor.setCell((long) value,d0index,d1index,d2index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsIntD4Tensor extends IntD4TensorShell implements WrappedTensor {
        private final LongD4Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsIntD4Tensor(LongD4Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsIntTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsIntD4Tensor (LongD4Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public int getCell(int d0index, int d1index, int d2index, int d3index) {
            return (int) tensor.getCell(d0index,d1index,d2index,d3index);
        }

        @Override
        public void setCell(int value, int d0index, int d1index, int d2index, int d3index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsIntD5Tensor extends IntD5TensorShell implements WrappedTensor {
        private final LongD5Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsIntD5Tensor(LongD5Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsIntTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsIntD5Tensor (LongD5Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return (int) tensor.getCell(d0index,d1index,d2index,d3index,d4index);
        }

        @Override
        public void setCell(int value, int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsIntD6Tensor extends IntD6TensorShell implements WrappedTensor {
        private final LongD6Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsIntD6Tensor(LongD6Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsIntTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsIntD6Tensor (LongD6Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return (int) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        @Override
        public void setCell(int value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsIntD7Tensor extends IntD7TensorShell implements WrappedTensor {
        private final LongD7Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsIntD7Tensor(LongD7Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsIntTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsIntD7Tensor (LongD7Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return (int) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        @Override
        public void setCell(int value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsIntD8Tensor extends IntD8TensorShell implements WrappedTensor {
        private final LongD8Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsIntD8Tensor(LongD8Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsIntTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsIntD8Tensor (LongD8Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return (int) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        @Override
        public void setCell(int value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsIntD9Tensor extends IntD9TensorShell implements WrappedTensor {
        private final LongD9Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsIntD9Tensor(LongD9Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsIntTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsIntD9Tensor (LongD9Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return (int) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        @Override
        public void setCell(int value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsIntTensor extends AbstractIntTensor implements WrappedTensor {
        private final LongTensor tensor;
        private final boolean wrappingWrapped;

        public LongAsIntTensor(LongTensor tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsIntTensor (LongTensor tensor) {
            this(tensor,false);
        }

        @Override
        public int getCell(int ... indices) {
            return (int) tensor.getCell(indices);
        }

        @Override
        public void setCell(int value, int ... indices) {
            tensor.setCell((long) value,indices);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsShortD0Tensor extends ShortD0TensorShell implements WrappedTensor {
        private final LongD0Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsShortD0Tensor(LongD0Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsShortTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsShortD0Tensor (LongD0Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public short getCell() {
            return (short) tensor.getCell();
        }

        @Override
        public void setCell(short value) {
            tensor.setCell((long) value);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsShortD1Tensor extends ShortD1TensorShell implements WrappedTensor {
        private final LongD1Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsShortD1Tensor(LongD1Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsShortTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsShortD1Tensor (LongD1Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public short getCell(int d0index) {
            return (short) tensor.getCell(d0index);
        }

        @Override
        public void setCell(short value, int d0index) {
            tensor.setCell((long) value,d0index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsShortD2Tensor extends ShortD2TensorShell implements WrappedTensor {
        private final LongD2Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsShortD2Tensor(LongD2Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsShortTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsShortD2Tensor (LongD2Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public short getCell(int d0index, int d1index) {
            return (short) tensor.getCell(d0index,d1index);
        }

        @Override
        public void setCell(short value, int d0index, int d1index) {
            tensor.setCell((long) value,d0index,d1index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsShortD3Tensor extends ShortD3TensorShell implements WrappedTensor {
        private final LongD3Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsShortD3Tensor(LongD3Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsShortTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsShortD3Tensor (LongD3Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public short getCell(int d0index, int d1index, int d2index) {
            return (short) tensor.getCell(d0index,d1index,d2index);
        }

        @Override
        public void setCell(short value, int d0index, int d1index, int d2index) {
            tensor.setCell((long) value,d0index,d1index,d2index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsShortD4Tensor extends ShortD4TensorShell implements WrappedTensor {
        private final LongD4Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsShortD4Tensor(LongD4Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsShortTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsShortD4Tensor (LongD4Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public short getCell(int d0index, int d1index, int d2index, int d3index) {
            return (short) tensor.getCell(d0index,d1index,d2index,d3index);
        }

        @Override
        public void setCell(short value, int d0index, int d1index, int d2index, int d3index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsShortD5Tensor extends ShortD5TensorShell implements WrappedTensor {
        private final LongD5Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsShortD5Tensor(LongD5Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsShortTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsShortD5Tensor (LongD5Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return (short) tensor.getCell(d0index,d1index,d2index,d3index,d4index);
        }

        @Override
        public void setCell(short value, int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsShortD6Tensor extends ShortD6TensorShell implements WrappedTensor {
        private final LongD6Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsShortD6Tensor(LongD6Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsShortTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsShortD6Tensor (LongD6Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return (short) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        @Override
        public void setCell(short value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsShortD7Tensor extends ShortD7TensorShell implements WrappedTensor {
        private final LongD7Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsShortD7Tensor(LongD7Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsShortTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsShortD7Tensor (LongD7Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return (short) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        @Override
        public void setCell(short value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsShortD8Tensor extends ShortD8TensorShell implements WrappedTensor {
        private final LongD8Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsShortD8Tensor(LongD8Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsShortTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsShortD8Tensor (LongD8Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return (short) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        @Override
        public void setCell(short value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsShortD9Tensor extends ShortD9TensorShell implements WrappedTensor {
        private final LongD9Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsShortD9Tensor(LongD9Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsShortTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsShortD9Tensor (LongD9Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return (short) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        @Override
        public void setCell(short value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsShortTensor extends AbstractShortTensor implements WrappedTensor {
        private final LongTensor tensor;
        private final boolean wrappingWrapped;

        public LongAsShortTensor(LongTensor tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsShortTensor (LongTensor tensor) {
            this(tensor,false);
        }

        @Override
        public short getCell(int ... indices) {
            return (short) tensor.getCell(indices);
        }

        @Override
        public void setCell(short value, int ... indices) {
            tensor.setCell((long) value,indices);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsByteD0Tensor extends ByteD0TensorShell implements WrappedTensor {
        private final LongD0Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsByteD0Tensor(LongD0Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsByteTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsByteD0Tensor (LongD0Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public byte getCell() {
            return (byte) tensor.getCell();
        }

        @Override
        public void setCell(byte value) {
            tensor.setCell((long) value);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsByteD1Tensor extends ByteD1TensorShell implements WrappedTensor {
        private final LongD1Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsByteD1Tensor(LongD1Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsByteTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsByteD1Tensor (LongD1Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public byte getCell(int d0index) {
            return (byte) tensor.getCell(d0index);
        }

        @Override
        public void setCell(byte value, int d0index) {
            tensor.setCell((long) value,d0index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsByteD2Tensor extends ByteD2TensorShell implements WrappedTensor {
        private final LongD2Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsByteD2Tensor(LongD2Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsByteTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsByteD2Tensor (LongD2Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public byte getCell(int d0index, int d1index) {
            return (byte) tensor.getCell(d0index,d1index);
        }

        @Override
        public void setCell(byte value, int d0index, int d1index) {
            tensor.setCell((long) value,d0index,d1index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsByteD3Tensor extends ByteD3TensorShell implements WrappedTensor {
        private final LongD3Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsByteD3Tensor(LongD3Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsByteTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsByteD3Tensor (LongD3Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public byte getCell(int d0index, int d1index, int d2index) {
            return (byte) tensor.getCell(d0index,d1index,d2index);
        }

        @Override
        public void setCell(byte value, int d0index, int d1index, int d2index) {
            tensor.setCell((long) value,d0index,d1index,d2index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsByteD4Tensor extends ByteD4TensorShell implements WrappedTensor {
        private final LongD4Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsByteD4Tensor(LongD4Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsByteTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsByteD4Tensor (LongD4Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public byte getCell(int d0index, int d1index, int d2index, int d3index) {
            return (byte) tensor.getCell(d0index,d1index,d2index,d3index);
        }

        @Override
        public void setCell(byte value, int d0index, int d1index, int d2index, int d3index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsByteD5Tensor extends ByteD5TensorShell implements WrappedTensor {
        private final LongD5Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsByteD5Tensor(LongD5Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsByteTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsByteD5Tensor (LongD5Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return (byte) tensor.getCell(d0index,d1index,d2index,d3index,d4index);
        }

        @Override
        public void setCell(byte value, int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsByteD6Tensor extends ByteD6TensorShell implements WrappedTensor {
        private final LongD6Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsByteD6Tensor(LongD6Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsByteTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsByteD6Tensor (LongD6Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return (byte) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        @Override
        public void setCell(byte value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsByteD7Tensor extends ByteD7TensorShell implements WrappedTensor {
        private final LongD7Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsByteD7Tensor(LongD7Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsByteTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsByteD7Tensor (LongD7Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return (byte) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        @Override
        public void setCell(byte value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsByteD8Tensor extends ByteD8TensorShell implements WrappedTensor {
        private final LongD8Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsByteD8Tensor(LongD8Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsByteTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsByteD8Tensor (LongD8Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return (byte) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        @Override
        public void setCell(byte value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsByteD9Tensor extends ByteD9TensorShell implements WrappedTensor {
        private final LongD9Tensor tensor;
        private final boolean wrappingWrapped;

        private LongAsByteD9Tensor(LongD9Tensor tensor, boolean wrappingWrapped) {
            super(new LongAsByteTensor(tensor));
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsByteD9Tensor (LongD9Tensor tensor) {
            this(tensor,false);
        }

        @Override
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return (byte) tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        @Override
        public void setCell(byte value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensor.setCell((long) value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    private static class LongAsByteTensor extends AbstractByteTensor implements WrappedTensor {
        private final LongTensor tensor;
        private final boolean wrappingWrapped;

        public LongAsByteTensor(LongTensor tensor, boolean wrappingWrapped) {
            super(tensor.getIndex());
            this.tensor = tensor;
            this.wrappingWrapped = wrappingWrapped;
        }

        public LongAsByteTensor (LongTensor tensor) {
            this(tensor,false);
        }

        @Override
        public byte getCell(int ... indices) {
            return (byte) tensor.getCell(indices);
        }

        @Override
        public void setCell(byte value, int ... indices) {
            tensor.setCell((long) value,indices);
        }

        public Tensor getWrappedTensor() {
            return wrappingWrapped ? ((WrappedTensor) tensor).getWrappedTensor() : tensor;
        }
    }

    //////////////////////copy to methods//////////////////////////
    static void copyTo(final Tensor<? extends Number> sourceTensor, DoubleTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.DoubleTensorValueFunction() {
            public double getValue(int ... indices) {
                return sourceTensor.getValue(indices).doubleValue();
            }
        });
    }
    
    static void copyTo(final ByteTensor sourceTensor, DoubleTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.DoubleTensorValueFunction() {
            public double getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final ShortTensor sourceTensor, DoubleTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.DoubleTensorValueFunction() {
            public double getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final IntTensor sourceTensor, DoubleTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.DoubleTensorValueFunction() {
            public double getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final LongTensor sourceTensor, DoubleTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.DoubleTensorValueFunction() {
            public double getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final FloatTensor sourceTensor, DoubleTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.DoubleTensorValueFunction() {
            public double getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final DoubleTensor sourceTensor, DoubleTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.DoubleTensorValueFunction() {
            public double getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }      
    
    static void copyTo(final Tensor<? extends Number> sourceTensor, FloatTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.FloatTensorValueFunction() {
            public float getValue(int ... indices) {
                return sourceTensor.getValue(indices).floatValue();
            }
        });
    }

    static void copyTo(final ByteTensor sourceTensor, FloatTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.FloatTensorValueFunction() {
            public float getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final ShortTensor sourceTensor, FloatTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.FloatTensorValueFunction() {
            public float getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final IntTensor sourceTensor, FloatTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.FloatTensorValueFunction() {
            public float getValue(int ... indices) {
                return (float) sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final LongTensor sourceTensor, FloatTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.FloatTensorValueFunction() {
            public float getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final DoubleTensor sourceTensor, FloatTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.FloatTensorValueFunction() {
            public float getValue(int ... indices) {
                return (float) sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final FloatTensor sourceTensor, FloatTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.FloatTensorValueFunction() {
            public float getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }      
    
    static void copyTo(final Tensor<? extends Number> sourceTensor, LongTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.LongTensorValueFunction() {
            public long getValue(int ... indices) {
                return sourceTensor.getValue(indices).longValue();
            }
        });
    }

    static void copyTo(final ByteTensor sourceTensor, LongTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.LongTensorValueFunction() {
            public long getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final ShortTensor sourceTensor, LongTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.LongTensorValueFunction() {
            public long getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final IntTensor sourceTensor, LongTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.LongTensorValueFunction() {
            public long getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final FloatTensor sourceTensor, LongTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.LongTensorValueFunction() {
            public long getValue(int ... indices) {
                return Math.round(sourceTensor.getCell(indices));
            }
        });
    }

    static void copyTo(final DoubleTensor sourceTensor, LongTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.LongTensorValueFunction() {
            public long getValue(int ... indices) {
                return Math.round(sourceTensor.getCell(indices));
            }
        });
    }

    static void copyTo(final LongTensor sourceTensor, LongTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.LongTensorValueFunction() {
            public long getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }      
    
    static void copyTo(final Tensor<? extends Number> sourceTensor, IntTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.IntTensorValueFunction() {
            public int getValue(int ... indices) {
                return sourceTensor.getValue(indices).intValue();
            }
        });
    }

    static void copyTo(final ByteTensor sourceTensor, IntTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.IntTensorValueFunction() {
            public int getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final ShortTensor sourceTensor, IntTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.IntTensorValueFunction() {
            public int getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final LongTensor sourceTensor, IntTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.IntTensorValueFunction() {
            public int getValue(int ... indices) {
                return (int) sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final FloatTensor sourceTensor, IntTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.IntTensorValueFunction() {
            public int getValue(int ... indices) {
                return Math.round(sourceTensor.getCell(indices));
            }
        });
    }

    static void copyTo(final DoubleTensor sourceTensor, IntTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.IntTensorValueFunction() {
            public int getValue(int ... indices) {
                return (int) Math.round(sourceTensor.getCell(indices));
            }
        });
    }

    static void copyTo(final IntTensor sourceTensor, IntTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.IntTensorValueFunction() {
            public int getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }      
    
    static void copyTo(final Tensor<? extends Number> sourceTensor, ShortTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ShortTensorValueFunction() {
            public short getValue(int ... indices) {
                return sourceTensor.getValue(indices).shortValue();
            }
        });
    }

    static void copyTo(final ByteTensor sourceTensor, ShortTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ShortTensorValueFunction() {
            public short getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final IntTensor sourceTensor, ShortTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ShortTensorValueFunction() {
            public short getValue(int ... indices) {
                return (short) sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final LongTensor sourceTensor, ShortTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ShortTensorValueFunction() {
            public short getValue(int ... indices) {
                return (short) sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final FloatTensor sourceTensor, ShortTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ShortTensorValueFunction() {
            public short getValue(int ... indices) {
                return (short) Math.round(sourceTensor.getCell(indices));
            }
        });
    }

    static void copyTo(final DoubleTensor sourceTensor, ShortTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ShortTensorValueFunction() {
            public short getValue(int ... indices) {
                return (short) Math.round(sourceTensor.getCell(indices));
            }
        });
    }

    static void copyTo(final ShortTensor sourceTensor, ShortTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ShortTensorValueFunction() {
            public short getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }        
    
    static void copyTo(final Tensor<? extends Number> sourceTensor, ByteTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ByteTensorValueFunction() {
            public byte getValue(int ... indices) {
                return sourceTensor.getValue(indices).byteValue();
            }
        });
    }

    static void copyTo(final ShortTensor sourceTensor, ByteTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ByteTensorValueFunction() {
            public byte getValue(int ... indices) {
                return (byte) sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final IntTensor sourceTensor, ByteTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ByteTensorValueFunction() {
            public byte getValue(int ... indices) {
                return (byte) sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final LongTensor sourceTensor, ByteTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ByteTensorValueFunction() {
            public byte getValue(int ... indices) {
                return (byte) sourceTensor.getCell(indices);
            }
        });
    }

    static void copyTo(final FloatTensor sourceTensor, ByteTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ByteTensorValueFunction() {
            public byte getValue(int ... indices) {
                return (byte) Math.round(sourceTensor.getCell(indices));
            }
        });
    }

    static void copyTo(final DoubleTensor sourceTensor, ByteTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ByteTensorValueFunction() {
            public byte getValue(int ... indices) {
                return (byte) Math.round(sourceTensor.getCell(indices));
            }
        });
    }

    static void copyTo(final ByteTensor sourceTensor, ByteTensor sinkTensor) {
        TensorImplUtil.checkSetTensorParameters(sinkTensor,sourceTensor);
        TensorUtil.fill(sinkTensor, new TensorUtil.ByteTensorValueFunction() {
            public byte getValue(int ... indices) {
                return sourceTensor.getCell(indices);
            }
        });
    }

    //////////////////////////eyes///////////////////////////////////////////
    private static class IdentityTensor extends AbstractIntTensor {
        //note: scalars explicitly will die if not wrapped in seperate class
        private static int[] dim(int dimensions, int size) {
            int[] dim = new int[dimensions];
            Arrays.fill(dim,size);
            return dim;
        }

        public IdentityTensor(int dimensions, int size) {
            super(dim(dimensions,size));
        }

        @Override
        public int getCell(int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            int last = indices[0];
            for (int i = 1; i < indices.length; i++)
                if (last != indices[i])
                    return 0;
            return 1;
        }

        @Override
        public void setCell(int value, int... indices) {
            throw new UnsupportedOperationException("Identity tensor is unmodifiable.");
        }
    }

    private static class IdentityMatrix extends IntD2TensorShell {
        public IdentityMatrix(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell(int d0Index, int d1Index) {  
            TensorImplUtil.checkIndexLengths(getDimensions(),d0Index,d1Index);
            return d0Index == d1Index ? 1 : 0;
        }

        @Override
        public void setCell(int value, int d0Index, int d1Index) {
            throw new UnsupportedOperationException("Identity matrix is unmodifiable.");
        }
    }    
    
    private static class IdentityVector extends IntD1TensorShell {
        public IdentityVector(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell(int d0Index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0Index);
            return 1;
        }

        @Override
        public void setCell(int value, int d0Index) {
            throw new UnsupportedOperationException("Identity vector is unmodifiable.");
        }
    }                                                               
    
    private static class IdentityScalar extends IntD0TensorShell {
        public IdentityScalar(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell() {
            return 1;
        }

        @Override
        public void setCell(int value) {
            throw new UnsupportedOperationException("Identity vector is unmodifiable.");
        }
    }

    public static IntTensor identityTensor(int dimensions, int size) {
        IntTensor identity = new IdentityTensor(dimensions,size);
        switch(dimensions) {
            case 0 : return new IdentityScalar(identity);
            case 1 : return new IdentityVector(identity);
            case 2 : return new IdentityMatrix(identity);
            case 3 : return new IntD3TensorShell(identity);
            case 4 : return new IntD4TensorShell(identity);
            case 5 : return new IntD5TensorShell(identity);
            case 6 : return new IntD6TensorShell(identity);
            case 7 : return new IntD7TensorShell(identity);
            case 8 : return new IntD8TensorShell(identity);
            case 9 : return new IntD9TensorShell(identity);
            default : return identity;
        }
    }

    //////////////////empty//////////////////////////
    private static class ZeroTensor extends AbstractIntTensor {

        public ZeroTensor(int ... dimensions) {
            super(dimensions);
        }

        @Override
        public int getCell(int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            return 0;
        }

        @Override
        public void setCell(int value, int... indices) {
            throw new UnsupportedOperationException("Zero tensor is unmodifiable.");
        }
    } 

    private static class ZeroScalar extends IntD0TensorShell {
        public ZeroScalar(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell() {
            return 0;
        }
    }

    private static class ZeroVector extends IntD1TensorShell {
        public ZeroVector(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell(int d0Index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0Index);
            return 0;
        }
    }

    private static class ZeroMatrix extends IntD2TensorShell {
        public ZeroMatrix(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell(int d0Index, int d1Index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0Index,d1Index);
            return 0;
        }
    }

    private static class ZeroD3Tensor extends IntD3TensorShell {
        public ZeroD3Tensor(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell(int d0Index, int d1Index, int d2Index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0Index,d1Index,d2Index);
            return 0;
        }
    }

    private static class ZeroD4Tensor extends IntD4TensorShell {
        public ZeroD4Tensor(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell(int d0Index, int d1Index, int d2Index, int d3Index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0Index,d1Index,d2Index,d3Index);
            return 0;
        }
    }  

    private static class ZeroD5Tensor extends IntD5TensorShell {
        public ZeroD5Tensor(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0Index,d1Index,d2Index,d3Index,d4Index);
            return 0;
        }
    }  

    private static class ZeroD6Tensor extends IntD6TensorShell {
        public ZeroD6Tensor(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0Index,d1Index,d2Index,d3Index,d4Index,d5Index);
            return 0;
        }
    }  

    private static class ZeroD7Tensor extends IntD7TensorShell {
        public ZeroD7Tensor(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index);
            return 0;
        }
    }  

    private static class ZeroD8Tensor extends IntD8TensorShell {
        public ZeroD8Tensor(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index);
            return 0;
        }
    }  

    private static class ZeroD9Tensor extends IntD9TensorShell {
        public ZeroD9Tensor(IntTensor tensor) {
            super(tensor);
        }

        @Override
        public int getCell(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index);
            return 0;
        }
    }  

    public static IntTensor zeroTensor(int ... dimensions) {
        IntTensor zero = new ZeroTensor(dimensions);
        switch(dimensions.length) {
            case 0 : return new ZeroScalar(zero);
            case 1 : return new ZeroVector(zero);
            case 2 : return new ZeroMatrix(zero);
            case 3 : return new ZeroD3Tensor(zero);
            case 4 : return new ZeroD4Tensor(zero);
            case 5 : return new ZeroD5Tensor(zero);
            case 6 : return new ZeroD6Tensor(zero);
            case 7 : return new ZeroD7Tensor(zero);
            case 8 : return new ZeroD8Tensor(zero);
            case 9 : return new ZeroD9Tensor(zero);
            default : return zero;
        }
    }
}
