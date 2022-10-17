package controller;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;

public class NoiseDetectionUtil {
    // --- https://stackoverflow.com/questions/3899585/microphone-level-in-java
    public static int calculateRMSLevel(byte[] audioData) {
        long lSum = 0;
        for (int i = 0; i < audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;
        double sumMeanSquare = 0d;

        for (int j = 0; j < audioData.length; j++)
            sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;

        return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
    }

    public static boolean detectNoise(int detectionTime) {

        //Audio Detection
        ByteArrayOutputStream byteArrayOutputStream;
        TargetDataLine targetDataLine;
        int cnt;
        byte tempBuffer[] = new byte[1024 * 4];
        int countzero, countdownTimer;
        short convert[] = new short[tempBuffer.length];

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            countdownTimer = 0;
            while (countdownTimer < detectionTime) {
                AudioFormat audioFormat = new AudioFormat(8000.0F, 16, 1, true, false);
                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
                targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                byteArrayOutputStream.write(tempBuffer, 0, cnt);

                int level = 0;
                if (targetDataLine.read(tempBuffer, 0, tempBuffer.length) > 0) {
                    level = calculateRMSLevel(tempBuffer);
                    if (level > 40) {
                        Thread.sleep(0);
                        targetDataLine.close();
                        return true;
                    }
                }

                try {
                    countzero = 0;
                    for (int i = 0; i < tempBuffer.length; i++) {
                        convert[i] = tempBuffer[i];
                        if (convert[i] == 0) {
                            countzero++;
                        }
                    }

                    countdownTimer++;

                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }
                Thread.sleep(0);
                targetDataLine.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }
}
