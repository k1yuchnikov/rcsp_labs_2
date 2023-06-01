import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.imageio.ImageIO;


public class ImageObj extends GraphicalObj {
    private Image image;


    private int vx = 3;
    private int vy = 3;


    public ImageObj (int x, int y, int width, int height, Color color, String imageUrl) throws IOException {
        super(x, y, width, height, color);
        URL url = new URL(imageUrl);
        image = ImageIO.read(url);
    }


    public void draw(Graphics g, int canvasWidth, int canvasHeight) {
        super.draw(g, canvasWidth, canvasHeight);


        g.drawImage(image, x - width/2, y - height/2, width, height, null);
    }


    @Override
    public boolean contains(int x, int y) {
        return (x >= this.x - width/2 && x <= this.x + width/2 && y >= this.y - height/2 && y <= this.y + height/2);
    }


    @Override
    public void read(InputStream input) throws IOException {
        DataInputStream dis = new DataInputStream(input);
        x = dis.readInt();
        y = dis.readInt();
        width = dis.readInt();
        height = dis.readInt();
        int r = dis.readInt();
        int g = dis.readInt();
        int b = dis.readInt();
        color = new Color(r, g, b);
        String imageUrl = dis.readUTF();
        URL url = new URL(imageUrl);
        image = ImageIO.read(url);
    }


    @Override
    public void write(OutputStream output) throws IOException {
        DataOutputStream dos = new DataOutputStream(output);
        dos.writeInt(x);
        dos.writeInt(y);
        dos.writeInt(width);
        dos.writeInt(height);
        dos.writeInt(color.getRed());
        dos.writeInt(color.getGreen());
        dos.writeInt(color.getBlue());
        dos.writeUTF(image.toString());
    }


    @Override
    public void move(int canvasWidth, int canvasHeight) {


        if (x + width / 2 >= canvasWidth || x - width / 2 <= 0) {
            vx *= -1;
        }


        if (y + height / 2 >= canvasHeight || y - height / 2 <= 0) {
            vy *= -1;
        }


        x += vx;
        y += vy;
    }
}


