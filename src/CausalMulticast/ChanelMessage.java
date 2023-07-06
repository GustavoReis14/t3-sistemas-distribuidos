package CausalMulticast;

import java.io.*;

public class ChanelMessage implements Serializable {
    private String content;
    private int[] clockArray;

    public ChanelMessage(String content, int[] clockArray) {
        this.content = content;
        this.clockArray = clockArray;
    }

    public String getContent() {
        return content;
    }

    public int[] getClockArray() {
        return clockArray;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);

        os.writeObject(this);

        return out.toByteArray();
    }
    public static ChanelMessage deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);

        return (ChanelMessage) is.readObject();
    }
}
