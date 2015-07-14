package com.pb.sawdust.model.integration.transcad;

import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.id.IdMatrix;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.group.StandardTensorGroup;
import com.pb.sawdust.tensor.group.TensorGroup;
import com.pb.sawdust.tensor.index.IndexFactory;
import com.pb.sawdust.tensor.read.SerializedTensorReader;
import com.pb.sawdust.tensor.write.SerializedTensorWriter;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.ProcessUtil;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

/**
 * The {@code TranscadNativeMatrixReader64Bit} ...
 *
 * @author crf
 *         Started 4/13/12 12:53 PM
 */
public class TranscadNativeMatrixReader64Bit<T> {
    public static final int DEFAULT_PORT = 46666;
    private static final String GROUP_MATRIX_CORE_NAME = "_ &&GROUP&& _";

    public static void main(final String ... args) {
        if (args.length == 11) {
            int maxWait = Integer.parseInt(args[9]);
            boolean throwMaxWaitException = Boolean.parseBoolean(args[10]);
            final String core = args[2];
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (core.equals(GROUP_MATRIX_CORE_NAME))
                        readAndSendMatrixGroup(Integer.parseInt(args[0]),Paths.get(args[1]),Integer.parseInt(args[3]),Integer.parseInt(args[4]),
                                               Integer.parseInt(args[5]),Integer.parseInt(args[6]),nullValueFromString(args[7],args[8]));
                    else
                        readAndSendMatrix(Integer.parseInt(args[0]),Paths.get(args[1]),core,Integer.parseInt(args[3]),Integer.parseInt(args[4]),
                                               Integer.parseInt(args[5]),Integer.parseInt(args[6]),nullValueFromString(args[7],args[8]));
                }
            });
            t.setDaemon(true);
            t.start();
            try {
                if (maxWait > 0)
                    t.join(maxWait);
                else
                    t.join();
                if (t.isAlive() && throwMaxWaitException)
                    throw new RuntimeException(String.format("Maximum time (%s seconds) elapsed for reading matrix %s from: %s",maxWait/1000,args[2],args[1]));
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
        } else {
            TranscadNativeMatrixReader64Bit t = new TranscadNativeMatrixReader64Bit(ArrayTensor.getFactory(),Paths.get("C:/Program Files (x86)/Java/jdk1.7.0/bin/java.exe"));
            Path matrix = Paths.get("D:\\projects\\reno\\model\\scenarios\\test4\\outputs\\skims\\street_skim.mtx");
            String core = "distance";
            t.setNullValue(Double.NaN);
            @SuppressWarnings("unchecked") //this is fine, and a test
            IdMatrix<Float,Integer> m = t.readMatrix(matrix,core,1,1);
//            Matrix<?> m = t.askAndReceiveMatrix(matrix,core,1,1);
            System.out.println(TensorUtil.toString(m));
        }
    }

    private final TensorFactory factory;
    private final int port;
    private final Path java32Executable;
    private final int maxMemory;
    private int dataWriteDelay = 1;
    private int dataWriteDelayFrequency = 100;
    private Number nullValue  = null;

    public int getPort() {
        return port;
    }

    public TranscadNativeMatrixReader64Bit(TensorFactory factory, Path java32Executable, int maxMemory, int port) {
        this.factory = factory;
        this.java32Executable = java32Executable;
        this.maxMemory = maxMemory;
        this.port = port;
    }

    public TranscadNativeMatrixReader64Bit(TensorFactory factory, Path java32Executable, int maxMemory) {
        this(factory,java32Executable,maxMemory,DEFAULT_PORT);
    }

    public TranscadNativeMatrixReader64Bit(TensorFactory factory, Path java32Executable) {
        this(factory,java32Executable,500,DEFAULT_PORT);
    }

    public void setWriteDelay(int delayInMillis, int delayFrequency) {
        dataWriteDelay = delayInMillis;
        dataWriteDelayFrequency = delayFrequency;
    }

    public void setNullValue(Number nullValue) {
        this.nullValue = nullValue;
    }

    private String nullValueToString() {
        if (nullValue == null)
            return "null";
        Class<?> c = nullValue.getClass();
        switch (JavaType.getPrimitiveJavaType(c)) {
            case BYTE :
            case SHORT :
            case INT :
            case LONG : return nullValue.toString();
            case FLOAT : return "" + Float.floatToIntBits((Float) nullValue);
            case DOUBLE: return "" + Double.doubleToLongBits((Double) nullValue);
        }
        throw new IllegalStateException("Shouldn't be here: " + nullValue);
    }

    private static Number nullValueFromString(String c, String value) {
        if (value.equals("null"))
            return null;
        try {
            switch (JavaType.getPrimitiveJavaType(Class.forName(c))) {
                case BYTE : return Byte.parseByte(value);
                case SHORT : return Short.parseShort(value);
                case INT : return Integer.parseInt(value);
                case LONG : return Long.parseLong(value);
                case FLOAT : return Float.intBitsToFloat(Integer.parseInt(value));
                case DOUBLE: return Double.longBitsToDouble(Long.parseLong(value));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeWrappingException(e);
        }
        throw new IllegalStateException("Shouldn't be here: " + c + ", " + value);
    }

    public IdMatrix<T,Integer> readMatrix(Path matrixFilePath, String core, int rowIndex, int columnIndex) {
        return readMatrix(matrixFilePath,core,rowIndex,columnIndex,-1,false);
    }

    @SuppressWarnings("unchecked") //deserialization will build the matrix of the correct type, so this is ok
    public IdMatrix<T,Integer> readMatrix(Path matrixFilePath, String core, int rowIndex, int columnIndex, int timeout, boolean throwMaxWaitException) {
        Process p = ProcessUtil.startProcess(Arrays.asList(java32Executable.toString(),"-Xmx" + maxMemory + "m","-cp",System.getProperty("java.class.path"),this.getClass().getCanonicalName(),
                                               "" + port,matrixFilePath.toString(),core,"" + rowIndex,"" + columnIndex,"" + dataWriteDelay,"" + dataWriteDelayFrequency,
                                               nullValue == null ? "null" : nullValue.getClass().getCanonicalName(),nullValueToString(),"" + timeout,"" + throwMaxWaitException));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
        try (Socket s = new Socket("localhost",port)) {
            s.setSoLinger(true,0);
            ObjectInputStream oi = new ObjectInputStream(s.getInputStream());
            return (IdMatrix<T,Integer>) SerializedTensorReader.deserializeTensor(factory,oi);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            p.destroy();
        }
    }

    public TensorGroup<T,Integer> readMatrixGroup(Path matrixFilePath, int rowIndex, int columnIndex) {
        return readMatrixGroup(matrixFilePath,rowIndex,columnIndex,-1,false);
    }

    public TensorGroup<T,Integer> readMatrixGroup(Path matrixFilePath, int rowIndex, int columnIndex, int timeout, boolean throwMaxWaitException) {
        Process p = ProcessUtil.startProcess(Arrays.asList(java32Executable.toString(),"-Xmx" + maxMemory + "m","-cp",System.getProperty("java.class.path"),this.getClass().getCanonicalName(),
                                               "" + port,matrixFilePath.toString(),GROUP_MATRIX_CORE_NAME,"" + rowIndex,"" + columnIndex,"" + dataWriteDelay,"" + dataWriteDelayFrequency,
                                               nullValue == null ? "null" : nullValue.getClass().getCanonicalName(),nullValueToString(),"" + timeout,"" + throwMaxWaitException));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
        try (Socket s = new Socket("localhost",port)) {
            s.setSoLinger(true,0);
            TensorGroup<T,Integer> group = null;
            ObjectInputStream oi = new ObjectInputStream(s.getInputStream());
            int matrixCount = oi.readInt();
            for (int i : range(matrixCount)) {
                String name = oi.readUTF();
                @SuppressWarnings("unchecked") //this is correct - assuming T is, and if not, an error will be raised shortly
                IdMatrix<T,Integer> matrix = (IdMatrix<T,Integer>) SerializedTensorReader.deserializeTensor(factory,oi);
                if (group == null)
                    group = new StandardTensorGroup<T,Integer>(matrix.getDimensions());
                group.addTensor(name,matrix);
            }
            return group;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            p.destroy();
        }
    }

    private static void readAndSendMatrix(int port, Path matrixFilePath, String core, int rowIndex, int columnIndex, int dataWriteDelay, int dataWriteDelayFrequency, Number nullValue) {
        TranscadNativeMatrixReader<?> reader = new TranscadNativeMatrixReader<>(matrixFilePath,core,rowIndex,columnIndex);
        if (nullValue != null)
            reader.setNullValue(nullValue);
        try ( ServerSocket ss = new ServerSocket(port);Socket s = ss.accept()) {
            s.setSoLinger(true,0);
            ObjectOutputStream oo = new ObjectOutputStream(s.getOutputStream());
            Tensor<?> t = ArrayTensor.getFactory().tensor(reader);
            SerializedTensorWriter.serializeTensor(t,oo,dataWriteDelay,dataWriteDelayFrequency);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private static void readAndSendMatrixGroup(int port, Path matrixFilePath, int rowIndex, int columnIndex, int dataWriteDelay, int dataWriteDelayFrequency, Number nullValue) {
        TranscadNativeMatrixReader<?> reader = new TranscadNativeMatrixReader<>(matrixFilePath,null,rowIndex,columnIndex);
        if (nullValue != null)
            reader.setNullValue(nullValue);
        try ( ServerSocket ss = new ServerSocket(port);Socket s = ss.accept()) {
            s.setSoLinger(true,0);
            ObjectOutputStream oo = new ObjectOutputStream(s.getOutputStream());
            @SuppressWarnings("unchecked") //casting ? to Object is ok here
            TensorGroup<?,Integer> matrixGroup = new StandardTensorGroup<>((TranscadNativeMatrixReader<Object>) reader,ArrayTensor.getFactory(),new IndexFactory());
            Set<String> matrixNames = matrixGroup.tensorKeySet();
            oo.writeInt(matrixNames.size());
            for (String name : matrixNames) {
                oo.writeUTF(name);
                SerializedTensorWriter.serializeTensor(matrixGroup.getTensor(name),oo,dataWriteDelay,dataWriteDelayFrequency);
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
