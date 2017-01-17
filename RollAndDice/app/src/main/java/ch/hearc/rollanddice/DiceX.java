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

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 1.0/1.1.
 */
public class DiceX {

    private final FloatBuffer mCubePositions;
    private final FloatBuffer mCubeColors;
    private final FloatBuffer mCubeNormals;
    private final FloatBuffer mCubeTextureCoordinates;

    // number of coordinates per vertex in this array
    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    private int x;
    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public DiceX(int x) {
        // Define points for a cube.
        this.x = (x >= 5) ? x : 5;

        int b = this.x - 2;

        float[] cubePositionData = new float[b*6*3 + (b-2)*3*6];

        double angle1 = 0;
        double angle2 = 0;
        for(int i = 0; i < b*6*3; i+= 3*6){
            angle2 += 2.0/(double)b*Math.PI;
            cubePositionData[i] = (float)Math.cos(angle1);
            cubePositionData[i+1] = -0.5f;
            cubePositionData[i+2] = (float)Math.sin(angle1);
            cubePositionData[i+3] = (float)Math.cos(angle2);
            cubePositionData[i+4] = 0.5f;
            cubePositionData[i+5] = (float)Math.sin(angle2);
            cubePositionData[i+6] = (float)Math.cos(angle1);
            cubePositionData[i+7] = 0.5f;
            cubePositionData[i+8] = (float)Math.sin(angle1);
            cubePositionData[i+9] = (float)Math.cos(angle2);
            cubePositionData[i+10] = 0.5f;
            cubePositionData[i+11] = (float)Math.sin(angle2);
            cubePositionData[i+12] = (float)Math.cos(angle1);
            cubePositionData[i+13] = -0.5f;
            cubePositionData[i+14] = (float)Math.sin(angle1);
            cubePositionData[i+15] = (float)Math.cos(angle2);
            cubePositionData[i+16] = -0.5f;
            cubePositionData[i+17] = (float)Math.sin(angle2);
            angle1 = angle2;
        }

        double angle0 = 0;
        angle1 = 2.0/(double)b*Math.PI;
        angle2 = angle1;
        for(int i = b*6*3; i < b*6*3 + (b-2)*3*6; i+= 3*6){
            angle2 += 2.0/(double)b*Math.PI;
            cubePositionData[i] = (float)Math.cos(angle0);
            cubePositionData[i+1] = 0.5f;
            cubePositionData[i+2] = (float)Math.sin(angle0);
            cubePositionData[i+3] = (float)Math.cos(angle1);
            cubePositionData[i+4] = 0.5f;
            cubePositionData[i+5] = (float)Math.sin(angle1);
            cubePositionData[i+6] = (float)Math.cos(angle2);
            cubePositionData[i+7] = 0.5f;
            cubePositionData[i+8] = (float)Math.sin(angle2);
            cubePositionData[i+9] = (float)Math.cos(angle0);
            cubePositionData[i+10] = -0.5f;
            cubePositionData[i+11] = (float)Math.sin(angle0);
            cubePositionData[i+12] = (float)Math.cos(angle1);
            cubePositionData[i+13] = -0.5f;
            cubePositionData[i+14] = (float)Math.sin(angle1);
            cubePositionData[i+15] = (float)Math.cos(angle2);
            cubePositionData[i+16] = -0.5f;
            cubePositionData[i+17] = (float)Math.sin(angle2);
            angle1 = angle2;
        }


        float[] cubeColorData = new float[(2*b-2)*6*4];

        for(int i = 0; i < (2*b-2)*6*4; i+= 6*4) {
            cubeColorData[i] = 1.0f;
            cubeColorData[i+1] = 0.0f;
            cubeColorData[i+2] = 0.0f;
            cubeColorData[i+3] = 0.5f;
            cubeColorData[i+4] = 1.0f;
            cubeColorData[i+5] = 0.0f;
            cubeColorData[i+6] = 0.0f;
            cubeColorData[i+7] = 1.0f;
            cubeColorData[i+8] = 1.0f;
            cubeColorData[i+9] = 0.0f;
            cubeColorData[i+10] = 0.0f;
            cubeColorData[i+11] = 1.0f;
            cubeColorData[i+12] = 0.0f;
            cubeColorData[i+13] = 0.0f;
            cubeColorData[i+14] = 1.0f;
            cubeColorData[i+15] = 1.0f;
            cubeColorData[i+16] = 1.0f;
            cubeColorData[i+17] = 0.0f;
            cubeColorData[i+18] = 0.0f;
            cubeColorData[i+19] = 1.0f;
            cubeColorData[i+20] = 1.0f;
            cubeColorData[i+21] = 0.0f;
            cubeColorData[i+22] = 0.0f;
            cubeColorData[i+23] = 1.0f;
        }


        float[] cubeNormalData = new float[b*6*3 + (b-2)*6*3];

        angle1 = 0;
        angle2 = 0;
        for(int i = 0; i < b*6*3; i+= 3*6){
            angle2 += 2.0/(double)b*Math.PI;
            double angleMid = (angle1+angle2)/2;
            cubeNormalData[i] = (float)Math.cos(angleMid);
            cubeNormalData[i+1] = 0;
            cubeNormalData[i+2] = (float)Math.sin(angleMid);
            cubeNormalData[i+3] = (float)Math.cos(angleMid);
            cubeNormalData[i+4] = 0;
            cubeNormalData[i+5] = (float)Math.sin(angleMid);
            cubeNormalData[i+6] = (float)Math.cos(angleMid);
            cubeNormalData[i+7] = 0;
            cubeNormalData[i+8] = (float)Math.sin(angleMid);
            cubeNormalData[i+9] = (float)Math.cos(angleMid);
            cubeNormalData[i+10] = 0;
            cubeNormalData[i+11] = (float)Math.sin(angleMid);
            cubeNormalData[i+12] = (float)Math.cos(angleMid);
            cubeNormalData[i+13] = 0;
            cubeNormalData[i+14] = (float)Math.sin(angleMid);
            cubeNormalData[i+15] = (float)Math.cos(angleMid);
            cubeNormalData[i+16] = 0;
            cubeNormalData[i+17] = (float)Math.sin(angleMid);
            angle1 = angle2;
        }

        for(int i = b*6*3; i < b*6*3 + (b-2)*6*3; i+= 3*6){
            cubeNormalData[i] = 0;
            cubeNormalData[i+1] = 1;
            cubeNormalData[i+2] = 0;
            cubeNormalData[i+3] = 0;
            cubeNormalData[i+4] = 1;
            cubeNormalData[i+5] = 0;
            cubeNormalData[i+6] = 0;
            cubeNormalData[i+7] = 1;
            cubeNormalData[i+8] = 0;
            cubeNormalData[i+9] = 0;
            cubeNormalData[i+10] = -1;
            cubeNormalData[i+11] = 0;
            cubeNormalData[i+12] = 0;
            cubeNormalData[i+13] = -1;
            cubeNormalData[i+14] = 0;
            cubeNormalData[i+15] = 0;
            cubeNormalData[i+16] = -1;
            cubeNormalData[i+17] = 0;
        }

        float[] cubeTextureCoordinateData = new float[b*6*2 + (b-2)*6*2];

        for(int i = 0; i < b*6*2 + (b-2)*6*2; i+= 6*2){
            cubeTextureCoordinateData[i] = 0.0f;
            cubeTextureCoordinateData[i+1] = 1.0f;
            cubeTextureCoordinateData[i+2] = 0.0f;
            cubeTextureCoordinateData[i+3] = 0.5f;
            cubeTextureCoordinateData[i+4] = 0.33f;
            cubeTextureCoordinateData[i+5] = 1.0f;
            cubeTextureCoordinateData[i+6] = 0.0f;
            cubeTextureCoordinateData[i+7] = 0.5f;
            cubeTextureCoordinateData[i+8] = 0.33f;
            cubeTextureCoordinateData[i+9] = 0.5f;
            cubeTextureCoordinateData[i+10] = 0.33f;
            cubeTextureCoordinateData[i+11] = 1.0f;
        }

        // Initialize the buffers.
        mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubePositions.put(cubePositionData).position(0);

        mCubeColors = ByteBuffer.allocateDirect(cubeColorData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeColors.put(cubeColorData).position(0);

        mCubeNormals = ByteBuffer.allocateDirect(cubeNormalData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeNormals.put(cubeNormalData).position(0);

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
    }



    /**
     * Draws a cube.
     */
    public void drawCube(MyGLRenderer g)
    {
        // Pass in the position information
        mCubePositions.position(0);
        GLES20.glVertexAttribPointer(g.mPositionHandle, g.mPositionDataSize, GLES20.GL_FLOAT, false,
                0, mCubePositions);

        GLES20.glEnableVertexAttribArray(g.mPositionHandle);

        // Pass in the color information
        mCubeColors.position(0);
        GLES20.glVertexAttribPointer(g.mColorHandle, g.mColorDataSize, GLES20.GL_FLOAT, false,
                0, mCubeColors);

        GLES20.glEnableVertexAttribArray(g.mColorHandle);

        // Pass in the normal information
        mCubeNormals.position(0);
        GLES20.glVertexAttribPointer(g.mNormalHandle, g.mNormalDataSize, GLES20.GL_FLOAT, false,
                0, mCubeNormals);

        GLES20.glEnableVertexAttribArray(g.mNormalHandle);

        // Pass in the texture coordinate information
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(g.mTextureCoordinateHandle, g.mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinates);

        GLES20.glEnableVertexAttribArray(g.mTextureCoordinateHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(g.mMVPMatrix, 0, g.mViewMatrix, 0, g.mModelMatrix, 0);

        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(g.mMVMatrixHandle, 1, false, g.mMVPMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(g.mMVPMatrix, 0, g.mProjectionMatrix, 0, g.mMVPMatrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(g.mMVPMatrixHandle, 1, false, g.mMVPMatrix, 0);

        // Pass in the light position in eye space.
        GLES20.glUniform3f(g.mLightPosHandle, g.mLightPosInEyeSpace[0], g.mLightPosInEyeSpace[1], g.mLightPosInEyeSpace[2]);

        GLES20.glDisable(GLES20.GL_CULL_FACE);

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, x*12-36 );
    }
}