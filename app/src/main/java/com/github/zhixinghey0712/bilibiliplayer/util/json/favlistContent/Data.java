package com.github.zhixinghey0712.bilibiliplayer.util.json.favlistContent;
import java.util.List;

public class Data {

    private Info info;
    private List<Medias> medias;
    public void setInfo(Info info) {
         this.info = info;
     }
     public Info getInfo() {
         return info;
     }

    public void setMedias(List<Medias> medias) {
         this.medias = medias;
     }
     public List<Medias> getMedias() {
         return medias;
     }

}