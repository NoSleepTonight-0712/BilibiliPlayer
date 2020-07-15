/**
  * Copyright 2020 bejson.com 
  */
package com.github.zhixingheyi0712.bilibiliplayer.util.json.downloadLink;
import java.util.List;

/**
 * Auto-generated: 2020-07-14 9:28:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Dash {

    private int duration;
    private double minBufferTime;
    private double min_buffer_time;
    private List<Video> video;
    private List<Audio> audio;
    public void setDuration(int duration) {
         this.duration = duration;
     }
     public int getDuration() {
         return duration;
     }

    public void setMinBufferTime(double minBufferTime) {
         this.minBufferTime = minBufferTime;
     }
     public double getMinBufferTime() {
         return minBufferTime;
     }

    public void setMin_buffer_time(double min_buffer_time) {
         this.min_buffer_time = min_buffer_time;
     }
     public double getMin_buffer_time() {
         return min_buffer_time;
     }

    public void setVideo(List<Video> video) {
         this.video = video;
     }
     public List<Video> getVideo() {
         return video;
     }

    public void setAudio(List<Audio> audio) {
         this.audio = audio;
     }
     public List<Audio> getAudio() {
         return audio;
     }

}