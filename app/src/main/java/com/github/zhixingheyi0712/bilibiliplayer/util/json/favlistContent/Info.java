package com.github.zhixingheyi0712.bilibiliplayer.util.json.favlistContent;

public class Info {

    private long id;
    private long fid;
    private long mid;
    private int attr;
    private String title;
    private String cover;
    private Upper upper;
    private int cover_type;
    private Cnt_info cnt_info;
    private int type;
    private String intro;
    private long ctime;
    private long mtime;
    private int state;
    private int fav_state;
    private int like_state;
    private int media_count;
    public void setId(long id) {
         this.id = id;
     }
     public long getId() {
         return id;
     }

    public void setFid(long fid) {
         this.fid = fid;
     }
     public long getFid() {
         return fid;
     }

    public void setMid(long mid) {
         this.mid = mid;
     }
     public long getMid() {
         return mid;
     }

    public void setAttr(int attr) {
         this.attr = attr;
     }
     public int getAttr() {
         return attr;
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

    public void setUpper(Upper upper) {
         this.upper = upper;
     }
     public Upper getUpper() {
         return upper;
     }

    public void setCover_type(int cover_type) {
         this.cover_type = cover_type;
     }
     public int getCover_type() {
         return cover_type;
     }

    public void setCnt_info(Cnt_info cnt_info) {
         this.cnt_info = cnt_info;
     }
     public Cnt_info getCnt_info() {
         return cnt_info;
     }

    public void setType(int type) {
         this.type = type;
     }
     public int getType() {
         return type;
     }

    public void setIntro(String intro) {
         this.intro = intro;
     }
     public String getIntro() {
         return intro;
     }

    public void setCtime(long ctime) {
         this.ctime = ctime;
     }
     public long getCtime() {
         return ctime;
     }

    public void setMtime(long mtime) {
         this.mtime = mtime;
     }
     public long getMtime() {
         return mtime;
     }

    public void setState(int state) {
         this.state = state;
     }
     public int getState() {
         return state;
     }

    public void setFav_state(int fav_state) {
         this.fav_state = fav_state;
     }
     public int getFav_state() {
         return fav_state;
     }

    public void setLike_state(int like_state) {
         this.like_state = like_state;
     }
     public int getLike_state() {
         return like_state;
     }

    public void setMedia_count(int media_count) {
         this.media_count = media_count;
     }
     public int getMedia_count() {
         return media_count;
     }

}