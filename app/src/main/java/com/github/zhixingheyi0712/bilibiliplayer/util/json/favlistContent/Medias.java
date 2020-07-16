package com.github.zhixingheyi0712.bilibiliplayer.util.json.favlistContent;

public class Medias {
    public int getPlay_times() {
        return play_times;
    }

    public void setPlay_times(int play_times) {
        this.play_times = play_times;
    }

    private int play_times;
    private long id;
    private int type;
    private String title;
    private String cover;
    private String intro;
    private int page;
    private int duration;
    private Upper upper;
    private int attr;
    private Cnt_info cnt_info;
    private String link;
    private long ctime;
    private long pubtime;
    private long fav_time;
    private String bv_id;
    private String bvid;
    public void setId(long id) {
         this.id = id;
     }
     public long getId() {
         return id;
     }

    public void setType(int type) {
         this.type = type;
     }
     public int getType() {
         return type;
     }

    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

    public void setCover(String cover) {
         this.cover = cover;
     }
     public String getCover() {
         return cover;
     }

    public void setIntro(String intro) {
         this.intro = intro;
     }
     public String getIntro() {
         return intro;
     }

    public void setPage(int page) {
         this.page = page;
     }
     public int getPage() {
         return page;
     }

    public void setDuration(int duration) {
         this.duration = duration;
     }
     public int getDuration() {
         return duration;
     }

    public void setUpper(Upper upper) {
         this.upper = upper;
     }
     public Upper getUpper() {
         return upper;
     }

    public void setAttr(int attr) {
         this.attr = attr;
     }
     public int getAttr() {
         return attr;
     }

    public void setCnt_info(Cnt_info cnt_info) {
         this.cnt_info = cnt_info;
     }
     public Cnt_info getCnt_info() {
         return cnt_info;
     }

    public void setLink(String link) {
         this.link = link;
     }
     public String getLink() {
         return link;
     }

    public void setCtime(long ctime) {
         this.ctime = ctime;
     }
     public long getCtime() {
         return ctime;
     }

    public void setPubtime(long pubtime) {
         this.pubtime = pubtime;
     }
     public long getPubtime() {
         return pubtime;
     }

    public void setFav_time(long fav_time) {
         this.fav_time = fav_time;
     }
     public long getFav_time() {
         return fav_time;
     }

    public void setBv_id(String bv_id) {
         this.bv_id = bv_id;
     }
     public String getBv_id() {
         return bv_id;
     }

    public void setBvid(String bvid) {
         this.bvid = bvid;
     }
     public String getBvid() {
         return bvid;
     }

}