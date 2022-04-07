/* TFLiteUtils.java
* 在该类中完成tensorflow lite的所有操作，包括预测，预处理，加载模型等
* function：
*   public TFLiteUtil(String modelPath) throws Exception {} 载入模型
*   public float[] predictImage(String image_path) throws Exception {} 选择图片预测方法，将图片路径转变成
*       位图作为模型的输入，开始预测
*   public float[] predictImage(Bitmap bitmap) throws Exception {} 视频流实时预测
*   private TensorImage loadImage(final Bitmap bitmap) {} 将位图数据预处理（归一化、标准化）
*   private float[] predict(Bitmap bmp) throws Exception {} 执行预测，返回概率值和标签
*   public static int getMaxResult(float[] result) {} 根据标签获取分类
*   */

package com.robertohuertas.endless;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.nnapi.NnApiDelegate;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;

public class TFLiteUtil {
    /*定义成员*/

    private static final String TAG = com.robertohuertas.endless.TFLiteUtil.class.getName();
    //创建tflite解释器
    private final Interpreter tflite;
    //图像输入buffer
    private TensorImage imageBuffer;
    //输出概率buffer
    private final TensorBuffer labelBuffer;
    //定义线程数（讲道理，一般的手机都有4线程吧？）
    private static final int NUM_THREADS = 4;
    //图像的均值、方差
    private static final float[] IMAGE_MEAN = new float[]{128.0f, 128.0f, 128.0f};
    private static final float[] IMAGE_STD = new float[]{128.0f, 128.0f, 128.0f};
    //定义tensorflow图像处理方法类，如裁剪和调整大小
    private final ImageProcessor imageProcessor;
    //定义神经网络模型的输入(输出)层名称
    private static final String InputName = "input";
    private static final String OutputName = "Identity";

    /**
     * @param modelPath model path
     */
    public TFLiteUtil(String modelPath) throws Exception{
        // 从模型路径读取出二进制模型
        File file = new File(modelPath);
        if(!file.exists()){
            throw new Exception("模型文件不存在！");
        }

        //配置tensorflow解释器
        try{
            Interpreter.Options options = (new Interpreter.Options());
            //多线程预测
            CompatibilityList compatList = new CompatibilityList();
            if(compatList.isDelegateSupportedOnThisDevice()){
                // if the device has a supported GPU, add the GPU delegate
                GpuDelegate.Options delegateOptions = compatList.getBestOptionsForThisDevice();
                GpuDelegate gpuDelegate = new GpuDelegate(delegateOptions);
                options.addDelegate(gpuDelegate);
                Log.d("su dung cai gi","GPU GPU GPU GPU GPU GPU");
            } else {
                // if the GPU is not supported, run on 4 threads
                Log.d("su dung cai gi","CPU CPU CPU CPU CPU CPU");
            options.setNumThreads(NUM_THREADS);
            //使用NNAPI代理，不采用GPU代理（nnapi支持的硬件包括cpu、gpu、npu）
            NnApiDelegate nnApiDelegate = null;
            // Initialize interpreter with NNAPI delegate for Android Pie or above
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                nnApiDelegate = new NnApiDelegate();
                options.addDelegate(nnApiDelegate);
            }
            }

            //创建解释器
            tflite = new Interpreter(file, options);
            //图片输入,shape->{1, h, w, 3}
            int[] imageShape = tflite.getInputTensor(tflite.getInputIndex(InputName)).shape();
            DataType imageDataType = tflite.getInputTensor(tflite.getInputIndex(InputName)).dataType();
            imageBuffer = new TensorImage(imageDataType);

            //标签输入,shape->{1, NUM_CLASSES}
            int[] labelShape = tflite.getOutputTensor(tflite.getOutputIndex(OutputName)).shape();
            DataType labelDataType = tflite.getOutputTensor(tflite.getOutputIndex(OutputName)).dataType();
            labelBuffer = TensorBuffer.createFixedSize(labelShape, labelDataType);

            // 添加图像预处理方式
            // resize双线性插值效果更好
            //
            imageProcessor = new ImageProcessor.Builder()
                    // Resize using Bilinear or Nearest neighbour
                    .add(new ResizeOp(imageShape[1], imageShape[2], ResizeOp.ResizeMethod.BILINEAR))
                    .add(new NormalizeOp(IMAGE_MEAN, IMAGE_STD))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("加载模型失败！");
        }
    }

    // 重载方法，根据图片路径转Bitmap预测
    public float[] predictImage(String image_path) throws Exception {
        if (!new File(image_path).exists()) {
            throw new Exception("ERR：图片不存在！");
        }
        FileInputStream fis = new FileInputStream(image_path);
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        float[] result = predictImage(bitmap);
        //回收bitmap，bitmap存在于buffer中,android在使用完以后没回收会出现问题
        if (bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return result;
    }

    // 重载方法，直接使用Bitmap预测(实时预测)
    public float[] predictImage(Bitmap bitmap) throws Exception {
        return predict(bitmap);
    }

    //数据预处理
    //bitmap->tensorImage
    private TensorImage loadImage(final Bitmap bitmap){
        imageBuffer.load(bitmap);
        return imageProcessor.process(imageBuffer);
    }

    //预测
    //送入位图，通过tflite.run执行预测，输出各种标签的预测概率值
    private float[] predict(Bitmap bitmap) throws Exception{
        imageBuffer = loadImage(bitmap);
//        Bitmap tmp = imageBuffer.getBitmap();

        try{
            tflite.run(imageBuffer.getBuffer(), labelBuffer.getBuffer().rewind());
        }catch (Exception e){
            throw new Exception("预测图像分类失败！log："+e);
        }

        //概率浮点数数组
        float[] probabilityArr = labelBuffer.getFloatArray();
        int maxIdxOfProbability = getMaxIndex(probabilityArr);
        if (maxIdxOfProbability==-1){
            throw new Exception("ERR：概率数组为空！");
        }
        return new float[]{maxIdxOfProbability, probabilityArr[maxIdxOfProbability]};
    }

    // 获取概率最大的标签
    public static int getMaxIndex(float[] result) {
        if(result==null||result.length==0){
            return -1;
        }
        //设置初始最大值索引为0
        int maxIdx = 0;
        for (int i=0;i<result.length-1;i++)
        {
            if (result[maxIdx]<result[i+1]){
                maxIdx = i+1;
            }
        }
        return maxIdx;
    }
}
