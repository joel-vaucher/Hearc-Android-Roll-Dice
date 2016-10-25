/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.hearc.rollanddice;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 1.0/1.1.
 */
public class Dice6 {

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;
    private final ShortBuffer drawListBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            -0.25f,  0.25f, 0.25f,   // top left front
            -0.25f, -0.25f, 0.25f,   // bottom left front
            0.25f, -0.25f, 0.25f,   // bottom right front
            0.25f,  0.25f, 0.25f, // top right front
            -0.25f,  0.25f, -0.25f,   // top left back
            -0.25f, -0.25f, -0.25f,   // bottom left back
            0.25f, -0.25f, -0.25f,   // bottom right back
            0.25f,  0.25f, -0.25f }; // top right back

    private final short drawOrder[] = { 0, 2, 1, 0, 2, 3, //front
                                        0, 5, 4, 0, 5, 1, //left
                                        0, 7, 4, 0, 7, 3, //top
                                        3, 6, 7, 3, 6, 2, //right
                                        1, 6, 2, 1, 6, 5, //bottom
                                        4, 6, 5, 4, 6, 7}; // order to draw vertices

    float color[] = { 1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f};

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Dice6() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize vertex byte buffer for shape coordinates
        bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                color.length * 4);
        bb.order(ByteOrder.nativeOrder());
        colorBuffer = bb.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param gl - The OpenGL ES context in which to draw this shape.
     */
    public void draw(GL10 gl) {
        // Since this shape uses vertex arrays, enable them
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        // draw the shape
        gl.glColorPointer(4,GL10.GL_FLOAT,0,colorBuffer);
        gl.glVertexPointer( // point to vertex data:
                COORDS_PER_VERTEX,
                GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawElements(  // draw shape:
                GL10.GL_TRIANGLES,
                drawOrder.length, GL10.GL_UNSIGNED_SHORT,
                drawListBuffer);

        // Disable vertex array drawing to avoid
        // conflicts with shapes that don't use it
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
}