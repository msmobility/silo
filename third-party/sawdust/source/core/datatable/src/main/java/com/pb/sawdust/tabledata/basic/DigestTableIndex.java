package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.AbstractTableIndex;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The {@code BasicDigestTableIndex} class provides a {@code TableIndex} implementation which uses message digests for
 * its unique indices. Specifically, indices are created by running the index values sequentially through the "SHA-1"
 * message digest algorithm to get a (hopefully) unique set of bytes.
 *
 * @author crf <br/>
 *         Started: Dec 24, 2008 11:26:59 AM
 */
public class DigestTableIndex<I> extends AbstractTableIndex<I, DigestTableIndex.ByteDigest> {
    private final MessageDigest md;

    /**
     * Constructor specifying the table and column labels for the index.
     *
     * @param table
     *        The table the index will apply to.
     *
     * @param indexColumnLabels
     *        The names of the columns the index will be based on.
     *
     * @throws com.pb.sawdust.tabledata.TableDataException if any of {@code indexColumnLabels} are not contained in {@code table}.
     */
    public DigestTableIndex(DataTable table, String ... indexColumnLabels) {
        super(table,indexColumnLabels);
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeWrappingException(e);
        }
    }

    @SuppressWarnings({"unchecked", "varargs"})
    protected ByteDigest buildIndexKey(I ... indexValues) {
        for (I value : indexValues)
            md.update(value.toString().getBytes());
        return new ByteDigest(md.digest());
    }

    //can't use byte[] (the digest) directly because it doesn't hash correctly
    /**
     * The {@code ByteDigest} class is a simple wrapper for the digests returned by the {@code MessageDigest} class.
     * This class is required because the hashing of {@code byte} arrays does not correctly account for the contents
     * of the arrays (<i>i.e.</i> if two arrays contain the same elements in the same order, their hash code - in the
     * context used here - should be identical).
     */
    public static final class ByteDigest {
        private final byte[] digest;

        /**
         * Constructor specifying the digest to wrap.
         *
         * @param digest
         *        The byte digest that will be wrapped.
         */
        public ByteDigest(byte[] digest) {
            this.digest = digest;
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof ByteDigest))
                return false;
            return MessageDigest.isEqual(((ByteDigest) o).digest,digest);
        }

        public int hashCode(){
            int result = 17;
            for (byte b : digest)
                result = 37*result + (int) b;
           return result;
        }
    }

//    public static class ByteDigest {
//        private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
//        private final String digest;
//
//        public ByteDigest(byte[] digest) {
//            this.digest = asHex(digest);
//        }
//
//        private static String asHex(byte[] digest) {
//            char[] chars = new char[2 * digest.length];
//            for (int i = 0; i < digest.length; ++i)
//            {
//                chars[2 * i] = HEX_CHARS[(digest[i] & 0xF0) >>> 4];
//                chars[2 * i + 1] = HEX_CHARS[digest[i] & 0x0F];
//            }
//            return new String(chars);
//        }
//
//        public boolean equals(Object o) {
//            if (o == this)
//                return true;
//            if (!(o instanceof ByteDigest))
//                return false;
//            ByteDigest d = (ByteDigest) o;
//            return digest.equals(d.digest);
//        }
//
//        public int hashCode() {
//            return digest.hashCode();
//        }
//    }
}
