import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.json.JSONObject;
import org.json.JSONArray;

public class Smiley extends GraphicalObject {
    private double angle = 0.0d;

    public Smiley(int x, int y, int width, int height, Color color) {
        super(x, y, width, height, color);
    }

    public void draw(Graphics g, int canvasWidth, int canvasHeight) {
        super.draw(g, canvasWidth, canvasHeight);

        Graphics2D g2d = (Graphics2D) g;

        AffineTransform originalTransform = g2d.getTransform(); // store the original transform
        AffineTransform rotatedTransform = AffineTransform.getRotateInstance(angle, x, y);
        g2d.transform(rotatedTransform); // apply the rotation

        g.setColor(Color.YELLOW);
        g.fillOval(x - width / 2, y - height / 2,width, height);

        // draw the eyes
        g.setColor(Color.BLACK);
        g.fillOval(x - width / 3, y - height / 3, width / 6, height / 6);
        g.fillOval(x + width / 6, y - height / 3, width / 6, height / 6);

        // draw the mouth
        g.drawArc(x - width / 4, y - height / 4, width / 2, height / 2, 190, 160);

        g2d.setTransform(originalTransform); // restore the original transform
    }

    @Override
    public boolean contains(int x, int y) {
        return (x >= this.x - width / 2 && x <= this.x + width / 2 && y >= this.y - height / 2 && y <= this.y + height / 2);
    }

    @Override
    public void read(InputStream input) throws IOException {
        DataInputStream dis = new DataInputStream(input);
        angle = dis.readDouble();
        x = dis.readInt();
        y = dis.readInt();
        width = dis.readInt();
        height = dis.readInt();
        int r = dis.readInt();
        int g = dis.readInt();
        int b = dis.readInt();
        color = new Color(r, g, b);

    }

    @Override
    public void write(OutputStream output) throws IOException {
        DataOutputStream dos = new DataOutputStream(output);
        dos.writeDouble(angle);
        dos.writeInt(x);
        dos.writeInt(y);
        dos.writeInt(width);
        dos.writeInt(height);
        dos.writeInt(color.getRed());
        dos.writeInt(color.getGreen());
        dos.writeInt(color.getBlue());
    }

    @Override
    public void move(int canvasWidth, int canvasHeight) {
        angle += 0.1;
    }

    @Override
    public String writeToJson() {
        var jsonObject = new JSONObject();

        jsonObject.put("angle", angle);
        jsonObject.put("x", x);
        jsonObject.put("y", y);
        jsonObject.put("width", width);
        jsonObject.put("height", height);
        jsonObject.put("color", color.getRGB());

        return jsonObject.toString();
    }

    @Override
    public void readFromJson(String json) {
        var jsonObject = new JSONObject(json);

        angle = jsonObject.getDouble("angle");
        x = jsonObject.getInt("x");
        y = jsonObject.getInt("y");
        width = jsonObject.getInt("width");
        height = jsonObject.getInt("height");
        color = new Color(jsonObject.getInt("color"));
    }

    @Override
    public String toString() {
        return "Smiley("
                + " x=" + x
                + " y=" + y
                + " color=" + "[" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + "]"
                + ")";
    }
}
