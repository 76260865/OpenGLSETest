/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.tomatozq.opengl.rubik;

import java.util.Arrays;
import java.util.Random;

import org.join.ogles.lib.AppConfig;

import com.tomatozq.opengl.tutorial.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class KubeActivity extends Activity implements KubeRenderer.AnimationCallback {	
    private GLWorld makeGLWorld()
    {
        GLWorld world = new GLWorld();
        GLShape.COUNT = 0;
        
        // coordinates for our cubes
        float c0 = -1.0f;
        float c1 = -0.38f;
        float c2 = -0.32f;
        float c3 = 0.32f;
        float c4 = 0.38f;
        float c5 = 1.0f;

        /*
         * 	0 1 2
         * 3 4 5
         *6 7 8 
         * */
        // top back, left to right
        mCubes[0]  = new Cube(world, c0, c4, c0, c1, c5, c1);
        mCubes[1]  = new Cube(world, c2, c4, c0, c3, c5, c1);
        mCubes[2]  = new Cube(world, c4, c4, c0, c5, c5, c1);
        // top middle, left to right
        mCubes[3]  = new Cube(world, c0, c4, c2, c1, c5, c3);
        mCubes[4]  = new Cube(world, c2, c4, c2, c3, c5, c3);
        mCubes[5]  = new Cube(world, c4, c4, c2, c5, c5, c3);
        // top front, left to right
        //-1,0.38,0.38,-0.38,1,1->-0.69,0.69,0.69
        mCubes[6]  = new Cube(world, c0, c4, c4, c1, c5, c5);
        mCubes[7]  = new Cube(world, c2, c4, c4, c3, c5, c5);
        mCubes[8]  = new Cube(world, c4, c4, c4, c5, c5, c5);
        // middle back, left to right
        mCubes[9]  = new Cube(world, c0, c2, c0, c1, c3, c1);
        mCubes[10] = new Cube(world, c2, c2, c0, c3, c3, c1);
        mCubes[11] = new Cube(world, c4, c2, c0, c5, c3, c1);
        // middle middle, left to right
        mCubes[12] = new Cube(world, c0, c2, c2, c1, c3, c3);
        //�м���Բ�����Ⱦ,�����㵽
        //mCubes[13] = new Cube(world, c2, c2, c2, c3, c3, c3);
        mCubes[13] = null;
        mCubes[14] = new Cube(world, c4, c2, c2, c5, c3, c3);
        // middle front, left to right
        mCubes[15] = new Cube(world, c0, c2, c4, c1, c3, c5);
        mCubes[16] = new Cube(world, c2, c2, c4, c3, c3, c5);
        mCubes[17] = new Cube(world, c4, c2, c4, c5, c3, c5);
        // bottom back, left to right
        mCubes[18] = new Cube(world, c0, c0, c0, c1, c1, c1);
        mCubes[19] = new Cube(world, c2, c0, c0, c3, c1, c1);
        mCubes[20] = new Cube(world, c4, c0, c0, c5, c1, c1);
        // bottom middle, left to right
        mCubes[21] = new Cube(world, c0, c0, c2, c1, c1, c3);
        mCubes[22] = new Cube(world, c2, c0, c2, c3, c1, c3);
        mCubes[23] = new Cube(world, c4, c0, c2, c5, c1, c3);
        // bottom front, left to right
        mCubes[24] = new Cube(world, c0, c0, c4, c1, c1, c5);
        mCubes[25] = new Cube(world, c2, c0, c4, c3, c1, c5);
        mCubes[26] = new Cube(world, c4, c0, c4, c5, c1, c5);

        // paint the sides
        int i, j;
        // set all faces black by default
        for (i = 0; i < 27; i++) {
            Cube cube = mCubes[i];
            if (cube != null) {
                for (j = 0; j < 6; j++)
                    cube.setFaceColor(j, GLColor.BLACK);
            }
        }

        // paint top
        for (i = 0; i < 9; i++){
            mCubes[i].setFaceColor(Cube.kTop, GLColor.ORANGE);
        }
        
        // paint bottom
        for (i = 18; i < 27; i++){
        	mCubes[i].setFaceColor(Cube.kBottom, GLColor.RED);
        }
        // paint left
        for (i = 0; i < 27; i += 3){
            mCubes[i].setFaceColor(Cube.kLeft, GLColor.YELLOW);
        }
        
        // paint right
        for (i = 2; i < 27; i += 3){
            mCubes[i].setFaceColor(Cube.kRight, GLColor.WHITE);
    	}
        
        // paint back
        for (i = 0; i < 27; i += 9){
            for (j = 0; j < 3; j++){
                mCubes[i + j].setFaceColor(Cube.kBack, GLColor.BLUE);
            }
    	}
    
        // paint front
        for (i = 6; i < 27; i += 9){
            for (j = 0; j < 3; j++){
                mCubes[i + j].setFaceColor(Cube.kFront, GLColor.GREEN);
            }
        }
        
        for (i = 0; i < 27; i++){
            if (mCubes[i] != null){
                world.addShape(mCubes[i]);
            }
            else{
            	//i=13Ϊ��
            	Log.i("KubeActivity", String.valueOf(i));
            }
        }
        
        // initialize our permutation to solved position
        mPermutation = new int[27];
        for (i = 0; i < mPermutation.length; i++)
            mPermutation[i] = i;

        createLayers();
        updateLayers();
        
        world.generate();

        return world;
    }

    private void createLayers() {
        mLayers[kUp] = new Layer(Layer.kAxisY,kUp);
        mLayers[kDown] = new Layer(Layer.kAxisY,kDown);
        mLayers[kLeft] = new Layer(Layer.kAxisX,kLeft);
        mLayers[kRight] = new Layer(Layer.kAxisX,kRight);
        mLayers[kFront] = new Layer(Layer.kAxisZ,kFront);
        mLayers[kBack] = new Layer(Layer.kAxisZ,kBack);
        mLayers[kMiddle] = new Layer(Layer.kAxisX,kMiddle);
        mLayers[kEquator] = new Layer(Layer.kAxisY,kEquator);
        mLayers[kSide] = new Layer(Layer.kAxisZ,kSide);
    }

    /**
     * ���¸����з���
     */
    private void updateLayers() {
        Layer layer;
        GLShape[] shapes;
        int i, j, k;

        // up layer
        layer = mLayers[kUp];
        shapes = layer.mShapes;
        for (i = 0; i < 9; i++){
            shapes[i] = mCubes[mPermutation[i]];
        }
        
        // equator layer
        layer = mLayers[kEquator];
        shapes = layer.mShapes;
        for (i = 9, k = 0; i < 18; i++){
            shapes[k++] = mCubes[mPermutation[i]];
        }
        
        // down layer
        layer = mLayers[kDown];
        shapes = layer.mShapes;
        for (i = 18, k = 0; i < 27; i++){
            shapes[k++] = mCubes[mPermutation[i]];
        }
        
        // left layer
        layer = mLayers[kLeft];
        shapes = layer.mShapes;
        for (i = 0, k = 0; i < 27; i += 9){
            for (j = 0; j < 9; j += 3){
                shapes[k++] = mCubes[mPermutation[i + j]];
            }
        }

        // middle layer
        layer = mLayers[kMiddle];
        shapes = layer.mShapes;
        for (i = 1, k = 0; i < 27; i += 9){
            for (j = 0; j < 9; j += 3){
                shapes[k++] = mCubes[mPermutation[i + j]];
            }
        }
        
        // right layer
        layer = mLayers[kRight];
        shapes = layer.mShapes;
        for (i = 2, k = 0; i < 27; i += 9){
            for (j = 0; j < 9; j += 3){
                shapes[k++] = mCubes[mPermutation[i + j]];
            }
    	}

        // front layer
        layer = mLayers[kFront];
        shapes = layer.mShapes;
        for (i = 6, k = 0; i < 27; i += 9){
            for (j = 0; j < 3; j++){
                shapes[k++] = mCubes[mPermutation[i + j]];
            }
        }

        // side layer
        layer = mLayers[kSide];
        shapes = layer.mShapes;
        for (i = 3, k = 0; i < 27; i += 9){
            for (j = 0; j < 3; j++){
                shapes[k++] = mCubes[mPermutation[i + j]];
            }
        }
        
        // back layer
        layer = mLayers[kBack];
        shapes = layer.mShapes;
        for (i = 0, k = 0; i < 27; i += 9){
            for (j = 0; j < 3; j++){
                shapes[k++] = mCubes[mPermutation[i + j]];
            }
        }
        
        StringBuilder sb = new StringBuilder();
		for (j = 0; j < mLayers[kFront].mShapes.length; j++) {
			if (mLayers[kFront].mShapes[j]!=null) {
				sb.append(mLayers[kFront].mShapes[j].id + ";");
			}
		}
		
//		Log.i("GLWorld", "front�������" + sb.toString());
//		printLayers();
    }

    private String [] mPreviousCube = new String[9];
    private boolean isValidated() {
        String [] cubes = new String[9];
        for (int i = 0; i <= 8; i++) {
            cubes[i] = mLayers[i].toString();
        }
        boolean ret = true;
//        for (int i = 0; i <= 8; i++) {
//            if (ret) {
//                for (int j = 0; j <= 8; j++) {
//                    if (mPreviousCube[j].equals(cubes[i])) {
//                        ret = true;
//                        break;
//                    } else {
//                        ret = false;
//                    }
//                    
//                }
//            }
//        }
        // �����ϴεĽ��:
//        for (int i = 0; i <= 8; i++) {
//            mPreviousCube[i] = prefs.getString(String.valueOf(i), mPreviousCube[i]);
//        }
        ret = Arrays.equals(mPreviousCube, cubes);
        Log.d("GLWorld", "isValidated: ret:" + ret + "\n" + "mPreviousCube:"
                + Arrays.toString(mPreviousCube)
                + " cubes:" + Arrays.toString(cubes));
        return ret;
    }

    SharedPreferences prefs;
    private void saveCurrentSate() {
        Editor editor =  prefs.edit();
        // �����ϴεĽ��:
        for (int i = 0; i <= 8; i++) {
            mPreviousCube[i] = mLayers[i].toString();
//            mLayers[i].setAngle(((float)Math.PI * i) / 2f);
            editor.putString(String.valueOf(i), mPreviousCube[i]);
        }
        editor.commit();
    }

    private void clearState() {
        Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    private void test() {
        layerID = mRandom.nextInt(8) ;
    }

    private void printLayers() {
        StringBuilder sb = new StringBuilder();
        sb.append("kUp�������" + mLayers[kUp].toString());
//        for (int j = 0; j < mLayers[kUp].mShapes.length; j++) {
//            if (mLayers[kUp].mShapes[j]!=null) {
//                sb.append(mLayers[kUp].mShapes[j].id + ";");
//            }
//        }
        sb.append("\n kEquator�������" + mLayers[kEquator].toString());
//        for (int j = 0; j < mLayers[kEquator].mShapes.length; j++) {
//            if (mLayers[kEquator].mShapes[j]!=null) {
//                sb.append(mLayers[kEquator].mShapes[j].id + ";");
//            }
//        }
        sb.append("\n kDown�������" + mLayers[kDown].toString());
//        for (int j = 0; j < mLayers[kDown].mShapes.length; j++) {
//            if (mLayers[kDown].mShapes[j]!=null) {
//                sb.append(mLayers[kDown].mShapes[j].id + ";");
//            }
//        }
        sb.append("\n kLeft�������" + mLayers[kLeft].toString());
//        for (int j = 0; j < mLayers[kLeft].mShapes.length; j++) {
//            if (mLayers[kLeft].mShapes[j]!=null) {
//                sb.append(mLayers[kLeft].mShapes[j].id + ";");
//            }
//        }
        sb.append("\n kMiddle�������" + mLayers[kMiddle].toString());
//        for (int j = 0; j < mLayers[kMiddle].mShapes.length; j++) {
//            if (mLayers[kMiddle].mShapes[j]!=null) {
//                sb.append(mLayers[kMiddle].mShapes[j].id + ";");
//            }
//        }
        sb.append("\n kRight�������" + mLayers[kRight].toString());
//        for (int j = 0; j < mLayers[kRight].mShapes.length; j++) {
//            if (mLayers[kRight].mShapes[j]!=null) {
//                sb.append(mLayers[kRight].mShapes[j].id + ";");
//            }
//        }
        sb.append("\n kFront�������" + mLayers[kFront].toString());
//        for (int j = 0; j < mLayers[kFront].mShapes.length; j++) {
//            if (mLayers[kFront].mShapes[j]!=null) {
//                sb.append(mLayers[kFront].mShapes[j].id + ";");
//            }
//        }
        sb.append("\n kSide�������" + mLayers[kSide].toString());
//        for (int j = 0; j < mLayers[kSide].mShapes.length; j++) {
//            if (mLayers[kSide].mShapes[j]!=null) {
//                sb.append(mLayers[kSide].mShapes[j].id + ";");
//            }
//        }
        sb.append("\n kBack�������" + mLayers[kBack].toString());
//        for (int j = 0; j < mLayers[kBack].mShapes.length; j++) {
//            if (mLayers[kBack].mShapes[j]!=null) {
//                sb.append(mLayers[kBack].mShapes[j].id + ";");
//            }
//        }
        
        Log.i("GLWorld", sb.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // We don't need a title either.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mRenderer = new KubeRenderer(makeGLWorld(), this);
        mView = new KubeSurfaceView(getApplication(),mRenderer);
        
        setContentView(mView);
        prefs = getPreferences(Context.MODE_PRIVATE);
        for (int i = 0; i <= 8; i++) {
            mPreviousCube[i] = prefs.getString(String.valueOf(i), mPreviousCube[i]);
        }
        Log.d("GLWorld","�����ֵΪ��" + Arrays.toString(mPreviousCube));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "����");
        menu.add(0, 2, 2, "����");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            clearState();
        } else if (item.getItemId() == 2) {
            saveCurrentSate();
        }
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mView.onPause();
    }

    public void animate() {
    	if (layerID==-1) {
			return;
		}
    	
        if (mCurrentLayer == null) {
            mCurrentLayer = mLayers[layerID];
            
            //mLayerPermutations��ʾ��תλ�ã�����λ�õķ�����ÿ����ת��ı�
            if (turningDirection) {
            	mCurrentLayerPermutation = mLayerCCWPermutations[layerID];
            }
            else{
            	mCurrentLayerPermutation = mLayerCWPermutations[layerID];
            }
        
            mCurrentLayer.startAnimation();
            
            int count = 1;
            
            mCurrentAngle = 0;
            
             if (turningDirection) {
                mAngleIncrement = (float)Math.PI / 50;
                   mEndAngle = mCurrentAngle + ((float)Math.PI * count) / 2f;
               } else {
                mAngleIncrement = -(float)Math.PI / 50;
                   mEndAngle = mCurrentAngle - ((float)Math.PI * count) / 2f;
            }
        }

         mCurrentAngle += mAngleIncrement;

         if ((mAngleIncrement > 0f && mCurrentAngle >= mEndAngle) ||
                 (mAngleIncrement < 0f && mCurrentAngle <= mEndAngle)) {
        	 //��ת��λ
             mCurrentLayer.setAngle(mEndAngle);
             mCurrentLayer.endAnimation();
             mCurrentLayer = null;
             layerID=-1;
             
             // adjust mPermutation based on the completed layer rotation
             int[] newPermutation = new int[27];
             for (int i = 0; i < 27; i++) {
                newPermutation[i] = mPermutation[mCurrentLayerPermutation[i]];
             }
             mPermutation = newPermutation;
             updateLayers();
             AppConfig.Turning = false;
            
//             Log.d("GLWorld","��ǰ�ϲ����з��飺" +  mLayers[0].toString());
//             Log.d("GLWorld","��ǰ�в����з��飺" +  mLayers[7].toString());
//             Log.d("GLWorld","��ǰ�²����з��飺" +  mLayers[1].toString());

             printLayers();
             
             if (!TextUtils.isEmpty(mPreviousCube[0]) && isValidated()) {
                 Intent intent = new Intent(this,MainActivity.class);
                 startActivity(intent);
             }
             mRenderer.clearPickedCubes();
         } else {
        	 //��ת��
             mCurrentLayer.setAngle(mCurrentAngle);
         }
    }

    GLSurfaceView mView;
    KubeRenderer mRenderer;
    Cube[] mCubes = new Cube[27];
    // a Layer for each possible move
    Layer[] mLayers = new Layer[9];
    // permutations corresponding to a  pi/2 clockwise rotation of each layer about its axis
    
    /*
     * ��ÿһ�㷽��λ�ý��б��(��������),��ת������һ��Ҫд�ԣ��������걣����뿴���Ĳ�ͬ
     * 0 1 2 					2 5 8
     * 3 4 5 ->˳ʱ��ѡ��90��->	1 4 7
     * 6 7 8					0 3 6
     * */
    static int[][] mLayerCWPermutations = {
        // permutation for UP layer ���ϲ�˳ʱ����ת90�Ⱥ󲼾�
        { 2, 5, 8, 1, 4, 7, 0, 3, 6, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 },
        // permutation for DOWN layer ���²�˳ʱ����ת90�Ⱥ󲼾�
        { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 20, 23, 26, 19, 22, 25, 18, 21, 24 },
        // permutation for LEFT layer �����ת90��
        { 6, 1, 2, 15, 4, 5, 24, 7, 8, 3, 10, 11, 12, 13, 14, 21, 16, 17, 0, 19, 20, 9, 22, 23, 18, 25, 26 },
        // permutation for RIGHT layer �Ҳ���ת90��
        { 0, 1, 8, 3, 4, 17, 6, 7, 26, 9, 10, 5, 12, 13, 14, 15, 16, 23, 18, 19, 2, 21, 22, 11, 24, 25, 20 },
        // permutation for FRONT layer ǰ����ת90��
        { 0, 1, 2, 3, 4, 5, 24, 15, 6, 9, 10, 11, 12, 13, 14, 25, 16, 7, 18, 19, 20, 21, 22, 23, 26, 17, 8 },
        // permutation for BACK layer ������ת90��
        { 18, 9, 0, 3, 4, 5, 6, 7, 8, 19, 10, 1, 12, 13, 14, 15, 16, 17, 20, 11, 2, 21, 22, 23, 24, 25, 26 },
        // permutation for MIDDLE layer ���м�����X����ת90�ȣ�
        { 0, 7, 2, 3, 16, 5, 6, 25, 8, 9, 4, 11, 12, 13, 14, 15, 22, 17, 18, 1, 20, 21, 10, 23, 24, 19, 26 },
        // permutation for EQUATOR layer (�м���Y����ת90��)
        { 0, 1, 2, 3, 4, 5, 6, 7, 8, 11, 14, 17, 10, 13, 16, 9, 12, 15, 18, 19, 20, 21, 22, 23, 24, 25, 26 },
        // permutation for SIDE layer (�м���Z����ת90��)
        { 0, 1, 2, 21, 12, 3, 6, 7, 8, 9, 10, 11, 22, 13, 4, 15, 16, 17, 18, 19, 20, 23, 14, 5, 24, 25, 26 }
};
    
    /*
     * ��ÿһ�㷽��λ�ý��б��(��������)��ת������һ��Ҫд�ԣ�
     * �������걣����뿴���Ĳ�ͬ(�����󷽿���ת���������ˣ�������ת�Ļ���ת)
     * 0 1 2 					6 3 0
     * 3 4 5 ->��ʱ��ѡ��90��->	7 4 1
     * 6 7 8					8 5 2
     * */
    static int[][] mLayerCCWPermutations = { 	
            // permutation for UP layer ���ϲ���ʱ����ת90�Ⱥ󲼾�
            { 6, 3, 0, 7, 4, 1, 8, 5, 2, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 },
            // permutation for DOWN layer ���²���ʱ����ת90�Ⱥ󲼾�
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 24, 21, 18, 25, 22, 19, 26, 23, 20},
            // permutation for LEFT layer ���
            { 18, 1, 2, 9, 4, 5, 0, 7, 8, 21, 10, 11, 12, 13, 14, 3, 16, 17, 24, 19, 20, 15, 22, 23, 6, 25, 26 },
            // permutation for RIGHT layer �Ҳ�
            { 0, 1, 20, 3, 4, 11, 6, 7, 2, 9, 10, 23, 12, 13, 14, 15, 16, 5, 18, 19, 26, 21, 22, 17, 24, 25, 8 },
            // permutation for FRONT layer ǰ��
            { 0, 1, 2, 3, 4, 5, 8, 17, 26, 9, 10, 11, 12, 13, 14, 7, 16, 25, 18, 19, 20, 21, 22, 23, 6, 15, 24 },
            // permutation for BACK layer ����
            { 2, 11, 20, 3, 4, 5, 6, 7, 8, 1, 10, 19, 12, 13, 14, 15, 16, 17, 0, 9, 18, 21, 22, 23, 24, 25, 26 },
            // permutation for MIDDLE layer ���м�����X����ʱ����ת��
            { 0, 19, 2, 3, 10, 5, 6, 1, 8, 9, 22, 11, 12, 13, 14, 15, 4, 17, 18, 25, 20, 21, 16, 23, 24, 7, 26 },
            // permutation for EQUATOR layer (�м���Y����ʱ����ת)
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 15, 12, 9, 16, 13, 10, 17, 14, 11, 18, 19, 20, 21, 22, 23, 24, 25, 26 },
            // permutation for SIDE layer	(�м���Z����ʱ����ת)
            { 0, 1, 2, 5, 14, 23, 6, 7, 8, 9, 10, 11, 4, 13, 22, 15, 16, 17, 18, 19, 20, 3, 12, 21, 24, 25, 26 }
    };

    // current permutation of starting position
    public int[] mPermutation;

    // for random cube movements
    Random mRandom = new Random(System.currentTimeMillis());
    //currently turning layerId;
    int layerID = -1;
    boolean turningDirection = false;
    
    // currently turning layer
    Layer mCurrentLayer = null;
    // current and final angle for current Layer animation
    float mCurrentAngle, mEndAngle;
    // amount to increment angle
    float mAngleIncrement;
    int[] mCurrentLayerPermutation;

    // names for our 9 layers (based on notation from http://www.cubefreak.net/notation.html)
    static final int kUp = 0;
    static final int kDown = 1;
    static final int kLeft = 2;
    static final int kRight = 3;
    static final int kFront = 4;
    static final int kBack = 5;
    static final int kMiddle = 6;
    static final int kEquator = 7;
    static final int kSide = 8;
}