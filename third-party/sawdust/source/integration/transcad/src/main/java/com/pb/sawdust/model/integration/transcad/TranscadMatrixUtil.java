package com.pb.sawdust.model.integration.transcad;

import com.pb.sawdust.io.ByteOrderDataInputStream;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.collections.InjectiveEnumHashMap;
import com.pb.sawdust.util.collections.InjectiveMap;
import transcad.DATA_TYPE;
import transcad.Matrix;
import transcad.Status;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code TranscadMatrixUtil} ...
 *
 * @author crf <br/>
 *         Started 4/21/11 7:53 AM
 */
class TranscadMatrixUtil {
    static String readPaddedString(ByteOrderDataInputStream inputStream, int length) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean inSkip = false;
        for (int i : range(length)) {
            byte value = inputStream.readByte();
            if (inSkip)
                continue;
            if (value == 0x00) {
                inSkip = true;
                continue;
            }
            sb.append((char) value);
        }
        return sb.toString();
    }

    static class SubIndexSpec {
        private final String name;
        private final boolean forRows;
        private final boolean forColumns;
        private final int length;
        private final boolean fullCoverage;

        public SubIndexSpec(String name, boolean forRows, boolean forColumns, int length, boolean fullCoverage) {
            this.name = name;
            this.forRows = forRows;
            this.forColumns = forColumns;
            this.length = length;
            this.fullCoverage = fullCoverage;
        }

        public String getName() {
            return name;
        }

        public boolean isForRows() {
            return forRows;
        }

        public boolean isForColumns() {
            return forColumns;
        }

        public int getLength() {
            return length;
        }

        public boolean isFullCoverage() {
            return fullCoverage;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("SubIndexSpec:\n");
            sb.append("    name: ").append(name).append("\n");
            sb.append("    forRows: ").append(forRows).append("\n");
            sb.append("    forColumns: ").append(forColumns).append("\n");
            sb.append("    length: ").append(length).append("\n");
            sb.append("    fullCoverage: ").append(fullCoverage);
            return sb.toString();
        }
    }

    public static void checkStatus(int statusCode, Matrix matrix) {
        if (statusCode != Status.TC_OKAY)
            throw new IllegalStateException("TransCAD DLL reported error (code " + statusCode + "): " + getStatusVariableName(statusCode) + "," + matrix.getStatusString());
    }

    public static void checkStatus(Matrix matrix) {
        TranscadMatrixUtil.checkStatus(matrix.getStatus(),matrix);
    }

    private static String getStatusVariableName(int status) {
            if (status == Status.TC_BADFIELD)
                return "TC_BADFIELD";
            else if (status == Status.TC_BADLOCKS)
                return "TC_BADLOCKS";
            else if (status == Status.TC_BADTYPE)
                return "TC_BADTYPE";
            else if (status == Status.TC_COMKEY)
                return "TC_COMKEY";
            else if (status == Status.TC_CORRUPT)
                return "TC_CORRUPT";
            else if (status == Status.TC_DBLACCESS)
                return "TC_DBLACCESS";
            else if (status == Status.TC_DBLERR)
                return "TC_DBLERR";
            else if (status == Status.TC_DBOPEN)
                return "TC_DBOPEN";
            else if (status == Status.TC_DEBUG)
                return "TC_DEBUG";
            else if (status == Status.TC_DELETED)
                return "TC_DELETED";
            else if (status == Status.TC_DELSYS)
                return "TC_DELSYS";
            else if (status == Status.TC_DLL_NOT_FOUND)
                return "TC_DLL_NOT_FOUND";
            else if (status == Status.TC_DUPLICATE)
                return "TC_DUPLICATE";
            else if (status == Status.TC_DUPUSERID)
                return "TC_DUPUSERID";
            else if (status == Status.TC_EOS)
                return "TC_EOS";
            else if (status == Status.TC_ESCAPED)
                return "TC_ESCAPED";
            else if (status == Status.TC_EXCLUSIVE)
                return "TC_EXCLUSIVE";
            else if (status == Status.TC_FAULT)
                return "TC_FAULT";
            else if (status == Status.TC_FPERROR)
                return "TC_FPERROR";
            else if (status == Status.TC_FSEEK)
                return "TC_FSEEK";
            else if (status == Status.TC_HASMEM)
                return "TC_HASMEM";
            else if (status == Status.TC_INCOMPAT)
                return "TC_INCOMPAT";
            else if (status == Status.TC_INTERRUPT)
                return "TC_INTERRUPT";
            else if (status == Status.TC_INVADDR)
                return "TC_INVADDR";
            else if (status == Status.TC_INVDB)
                return "TC_INVDB";
            else if (status == Status.TC_INVFILE)
                return "TC_INVFILE";
            else if (status == Status.TC_INVFLD)
                return "TC_INVFLD";
            else if (status == Status.TC_INVINPUT)
                return "TC_INVINPUT";
            else if (status == Status.TC_INVMEM)
                return "TC_INVMEM";
            else if (status == Status.TC_INVNUM)
                return "TC_INVNUM";
            else if (status == Status.TC_INVOWN)
                return "TC_INVOWN";
            else if (status == Status.TC_INVREC)
                return "TC_INVREC";
            else if (status == Status.TC_INVSET)
                return "TC_INVSET";
            else if (status == Status.TC_ISCOMKEY)
                return "TC_ISCOMKEY";
            else if (status == Status.TC_ISMEM)
                return "TC_ISMEM";
            else if (status == Status.TC_ISOWNED)
                return "TC_ISOWNED";
            else if (status == Status.TC_KEYERR)
                return "TC_KEYERR";
            else if (status == Status.TC_KEYREQD)
                return "TC_KEYREQD";
            else if (status == Status.TC_KEYSEQ)
                return "TC_KEYSEQ";
            else if (status == Status.TC_LMBUSY)
                return "TC_LMBUSY";
            else if (status == Status.TC_LOCKED)
                return "TC_LOCKED";
            else if (status == Status.TC_MISSING)
                return "TC_MISSING";
            else if (status == Status.TC_MOVED)
                return "TC_MOVED";
            else if (status == Status.TC_NAMELEN)
                return "TC_NAMELEN";
            else if (status == Status.TC_NETERR)
                return "TC_NETERR";
            else if (status == Status.TC_NETSYNC)
                return "TC_NETSYNC";
            else if (status == Status.TC_NOCM)
                return "TC_NOCM";
            else if (status == Status.TC_NOCO)
                return "TC_NOCO";
            else if (status == Status.TC_NOCR)
                return "TC_NOCR";
            else if (status == Status.TC_NODEVICE)
                return "TC_NODEVICE";
            else if (status == Status.TC_NOFILE)
                return "TC_NOFILE";
            else if (status == Status.TC_NOLOCKMGR)
                return "TC_NOLOCKMGR";
            else if (status == Status.TC_NOMEMORY)
                return "TC_NOMEMORY";
            else if (status == Status.TC_NOROOM)
                return "TC_NOROOM";
            else if (status == Status.TC_NOSPACE)
                return "TC_NOSPACE";
            else if (status == Status.TC_NOTCON)
                return "TC_NOTCON";
            else if (status == Status.TC_NOTFOUND)
                return "TC_NOTFOUND";
            else if (status == Status.TC_NOTFREE)
                return "TC_NOTFREE";
            else if (status == Status.TC_NOTKEY)
                return "TC_NOTKEY";
            else if (status == Status.TC_NOTLOCKED)
                return "TC_NOTLOCKED";
            else if (status == Status.TC_NOTOPTKEY)
                return "TC_NOTOPTKEY";
            else if (status == Status.TC_NOTRANS)
                return "TC_NOTRANS";
            else if (status == Status.TC_NOUNDO)
                return "TC_NOUNDO";
            else if (status == Status.TC_NOWORK)
                return "TC_NOWORK";
            else if (status == Status.TC_OKAY)
                return "TC_OKAY";
            else if (status == Status.TC_READ)
                return "TC_READ";
            else if (status == Status.TC_RECLIMIT)
                return "TC_RECLIMIT";
            else if (status == Status.TC_RECOVERY)
                return "TC_RECOVERY";
            else if (status == Status.TC_RENAME)
                return "TC_RENAME";
            else if (status == Status.TC_RESIZE)
                return "TC_RESIZE";
            else if (status == Status.TC_RUNTIME_EXCEPTION)
                return "TC_RUNTIME_EXCEPTION";
            else if (status == Status.TC_SETPAGES)
                return "TC_SETPAGES";
            else if (status == Status.TC_STATIC)
                return "TC_STATIC";
            else if (status == Status.TC_SYSERR)
                return "TC_SYSERR";
            else if (status == Status.TC_TIMESTAMP)
                return "TC_TIMESTAMP";
            else if (status == Status.TC_TRACTIVE)
                return "TC_TRACTIVE";
            else if (status == Status.TC_TRANSID)
                return "TC_TRANSID";
            else if (status == Status.TC_TRCHANGES)
                return "TC_TRCHANGES";
            else if (status == Status.TC_TRFREE)
                return "TC_TRFREE";
            else if (status == Status.TC_TRLOCKS)
                return "TC_TRLOCKS";
            else if (status == Status.TC_TRNOTACT)
                return "TC_TRNOTACT";
            else if (status == Status.TC_UNAVAIL)
                return "TC_UNAVAIL";
            else if (status == Status.TC_UNLOCKED)
                return "TC_UNLOCKED";
            else if (status == Status.TC_UPDATED)
                return "TC_UPDATED";
            else if (status == Status.TC_USERID)
                return "TC_USERID";
            else if (status == Status.TC_USERLIMIT)
                return "TC_USERLIMIT";
            else if (status == Status.TC_WRITE)
                return "TC_WRITE";
            return "unknown error";
        }
}
