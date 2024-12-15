package renderer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.stb.STBImage.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture2D {
    private int id;
    private int width, height, bpp;
    private int internalFormat;
    private int dataFormat;

    public Texture2D(String filepath) {
        IntBuffer pWidth = BufferUtils.createIntBuffer(1);
        IntBuffer pHeight = BufferUtils.createIntBuffer(1);
        IntBuffer pBpp = BufferUtils.createIntBuffer(1);

        ByteBuffer image = stbi_load(filepath, pWidth, pHeight, pBpp, 0);
        assert image != null : "Failed to load texture '" + filepath + "'";

        stbi_set_flip_vertically_on_load(true);

        width = pWidth.get(0);
        height = pHeight.get(0);
        bpp = pBpp.get(0);

        switch (bpp)
        {
            case 4:
                internalFormat = GL_RGBA8;
                dataFormat = GL_RGBA;
                break;
            case 3:
                internalFormat = GL_RGB8;
                dataFormat = GL_RGB;
                break;
        }

        id = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(id, 1, internalFormat, width, height);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTextureSubImage2D(id, 0, 0, 0, width, height, dataFormat, GL_UNSIGNED_BYTE, image);
        stbi_image_free(image);
    }

    public void setData(ByteBuffer data, int size) {
        int bpp = dataFormat == GL_RGBA ? 4 : 3;
        assert size == width * height * bpp : "Data must be the entire Texture";
        glTextureSubImage2D(id, 0, 0, 0, width, height, dataFormat, GL_UNSIGNED_BYTE, data);
    }

    public int getId() {
        return id;
    }

    public static void bind(int slot, int id) {
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
