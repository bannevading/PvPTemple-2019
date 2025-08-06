package server.pvptemple.util.finalutil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class FontRenderer {
   private int[] charOffsets = new int[256];
   private int[] charWidths = new int[256];
   private int textureHeight;
   private int textureWidth;

   FontRenderer() {
      BufferedImage fontTexture;
      try {
         fontTexture = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("default.png"));
      } catch (IOException e) {
         e.printStackTrace();
         return;
      }

      int width = fontTexture.getWidth();
      int height = fontTexture.getHeight();
      this.textureWidth = width;
      this.textureHeight = height;
      this.calculateCharWidths(fontTexture, width, height);
   }

   private static boolean isColEmpty(int[] imgData, int offset, int imageWidth, int maxCharHeight) {
      for(int row = 0; row < maxCharHeight; ++row) {
         int rowOffset = offset + row * imageWidth;
         if ((imgData[rowOffset] >> 24 & 255) > 128) {
            return false;
         }
      }

      return true;
   }

   private void calculateCharWidths(BufferedImage fontTexture, int width, int height) {
      int[] fontData = new int[width * height];
      fontTexture.getRGB(0, 0, width, height, fontData, 0, width);
      int maxCharWidth = width / 16;
      int maxCharHeight = height / 16;

      for(int character = 0; character < 128; ++character) {
         int col = character % 16;
         int row = character / 16;
         int offset = col * maxCharWidth + row * maxCharHeight * width;
         if (character == 32) {
            this.charWidths[32] = maxCharWidth / 3;
         } else {
            int chStart = 0;

            for(int c = 0; c < maxCharWidth; ++c) {
               chStart = c;
               if (!isColEmpty(fontData, offset + c, width, maxCharHeight)) {
                  break;
               }
            }

            int chEnd = maxCharWidth - 1;

            for(int c = maxCharWidth - 1; c >= chStart; --c) {
               chEnd = c;
               if (!isColEmpty(fontData, offset + c, width, maxCharHeight)) {
                  break;
               }
            }

            this.charOffsets[character] = chStart;
            this.charWidths[character] = chEnd - chStart + 1;
         }
      }

   }

   public int getWidth(String text) {
      if (text == null) {
         return 0;
      } else {
         float charWidthScale = 128.0F / (float)this.textureWidth;
         float width = 0.0F;

         for(int j = 0; j < text.length(); ++j) {
            int k = text.charAt(j);
            if (k == 38) {
               ++j;
            } else {
               width += (float)this.charWidths[k] * charWidthScale + 1.0F;
            }
         }

         return (int)Math.ceil((double)width);
      }
   }

   public int getHeight() {
      return (int)Math.ceil((double)this.textureHeight);
   }
}
