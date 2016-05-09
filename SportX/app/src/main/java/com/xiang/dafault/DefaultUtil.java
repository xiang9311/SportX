package com.xiang.dafault;

import com.xiang.proto.nano.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 祥祥 on 2016/4/18.
 */
public class DefaultUtil {

    public static List<Common.Comment> getComments(int count){
        List<Common.Comment> comments = new ArrayList<>();
        String[] coms = new String[]{"系统还提供了相关值TextAppearance_Small, TextAppearance_Large等。如有需要可在以上样式基础上修改。"
                ,"Here are all the FloatingActionMenu's xml attributes with their default values which means that you don't have to set all of them:"};
        String[] name = new String[]{"欧意健身房","吊炸天健身房","乐美多健身房","工体游泳馆","海淀最大游泳馆","北京体育场"};
        for (int i = 0; i < count; i ++){
            Common.Comment comment = new Common.Comment();
            comment.briefUser = getBriefUser();
            comment.commentId = 0;
            comment.commentContent = coms[getRandom(2)];
            comment.trendId = 0;
            comment.toUserid = getRandom(5);
            comment.toUserName = "张伟要上天";
            comment.createTime = System.currentTimeMillis() - 1000*getRandom(40);
            comment.gymName = name[getRandom(6)];
            comments.add(comment);
        }
        return comments;
    }

    public static List<Common.BriefGym> getGyms(int count){
        String[] gymcover = new String[]{
                "http://pic13.nipic.com/20110301/5713677_135704571127_2.jpg",
                "http://www.mangocity.com/vacations/vimg/trip/big_197343_1332488549879.JPG",
                "http://wc.zshl.com/datashow/picb/2005/p2005348171428875.jpg"
        };
        String[] name = new String[]{"欧意健身房","吊炸天健身房","乐美多健身房","工体游泳馆","海淀最大游泳馆","北京体育场"};
        List<Common.BriefGym> gyms = new ArrayList<>();
        for(int i = 0; i < count; i ++){
            Common.BriefGym gym = new Common.BriefGym();
            gym.id = 0;
            gym.gymName = name[getRandom(6)];
            gym.gymAvatar = gymcover[getRandom(3)];
            gym.gymCover = new String[]{gymcover[getRandom(3)], gymcover[getRandom(3)], gymcover[getRandom(3)]
                    , gymcover[getRandom(3)], gymcover[getRandom(3)], gymcover[getRandom(3)]};
            gym.place = "海淀区海淀黄庄4号楼2503";
            gym.isCoop = getRandom(2) == 1;
            gyms.add(gym);
        }
        return gyms;
    }

    public static List<Common.DetailGym> getDetailGyms(int count){
        List<Common.DetailGym> detailGyms = new ArrayList<>();
        for(int i = 0; i < count; i ++){
            detailGyms.add(getDetailGym());
        }
        return detailGyms;
    }

    public static Common.DetailGym getDetailGym(){
        Common.DetailGym detailGym = new Common.DetailGym();
        detailGym.briefGym = getGyms(1).get(0);
        detailGym.equipments = getEquipments(6);
        detailGym.courses = getCourse(4);
        detailGym.gymCards = getGymCard(3);
        return detailGym;
    }

    public static Common.Equipment[] getEquipments(int count){
        String[] names = new String[]{"跑步机", "俯卧撑", "哑铃", "空气净化器", "游泳池", "洗澡间"};
        Common.Equipment[] equipments = new Common.Equipment[count];
        for(int i = 0; i < count; i ++) {
            Common.Equipment equipment = new Common.Equipment();
            equipment.name = names[getRandom(6)];
            equipment.count = getRandom(6);
            equipment.equipmentType = Common.PAO_BU_JI;
            equipments[i] = equipment;
        }
        return equipments;
    }

    public static Common.Course[] getCourse(int count){
        String[] names = new String[]{"瑜伽", "普拉提", "健美操", "HIPHOP"};
        Common.Course[] courses = new Common.Course[count];
        for(int i = 0; i < count; i ++) {
            Common.Course course = new Common.Course();
            course.name = names[getRandom(4)];
            course.week = getRandom(7) + 1;
            Common.CourseTime courseTime = new Common.CourseTime();
            int fh = getRandom(18);
            courseTime.fromHour = fh;
            courseTime.fromMinite = 0;
            courseTime.toHour = fh + getRandom(2);
            courseTime.toMinite = 0;
            course.courseTime = courseTime;
            courses[i] = course;
        }
        return courses;
    }

    public static Common.GymCard[] getGymCard(int count){

        Common.GymCard[] cards = new Common.GymCard[count];
        if(count > 0){
            Common.GymCard gymCard = new Common.GymCard();
            gymCard.cardType = Common.Once;
            gymCard.price = getRandom(100);
            cards[0] = gymCard;
        }
        if(count > 1){
            Common.GymCard gymCard = new Common.GymCard();
            gymCard.cardType = Common.Month;
            gymCard.price = getRandom(500);
            cards[1] = gymCard;
        }
        if(count > 2){
            Common.GymCard gymCard = new Common.GymCard();
            gymCard.cardType = Common.Quarter;
            gymCard.price = getRandom(1000);
            cards[2] = gymCard;
        }
        return cards;
    }

    public static List<Common.Banner> getBanner(int count){
        List<Common.Banner> banners = new ArrayList<>();
        for(int i = 0; i < count; i ++){
            Common.Banner banner = new Common.Banner();
            banner.id = 0;
            banner.coverUrl = "http://img2.imgtn.bdimg.com/it/u=3869342028,2411605356&fm=21&gp=0.jpg";
            banner.type = getRandom(2);
            banner.webUrl = "http://ask.csdn.net/questions/157520";
            banner.trendId = 0;
            banner.userId = 0;
            banners.add(banner);
        }
        return banners;
    }

    public static List<Common.Trend> getTrends(int count){
        String[] images = new String[]{"http://www.feizl.com/upload2007/2011_09/110907154639871.jpg",
                "http://v1.qzone.cc/avatar/201310/03/13/18/524cfe3d23517873.jpg%21200x200.jpg",
                "http://pic13.nipic.com/20110301/5713677_135704571127_2.jpg",
                "http://wc.zshl.com/datashow/picb/2005/p2005348171428875.jpg",
                "http://wc.zshl.com/datashow/picb/2005/p2005348171428875.jpg",
                "http://www.feizl.com/upload2007/2011_09/110907154639871.jpg"};
        List<Common.Trend> trends = new ArrayList<>();
        for(int i = 0; i < count; i ++){
            Common.Trend trend = new Common.Trend();
            trend.id = 0;
            trend.briefUser = getBriefUser();
            trend.createTime = System.currentTimeMillis() - 10000 * getRandom(8);
            trend.gymId = 0;
            trend.gymName = "派力司健身";
            trend.content = "最后，如果需要设置某控件跨越多行或多列，只需将该子控件的android:layout_rowSpan或者layout_columnSpan属性设置为数值，再设置其layout_gravity属性为fill即可，前一个设置表明该控件跨越的行数或列数，后一个设置表明该控件填满所跨越的整行或整列。";
            trend.likeCount = getRandom(100);
            trend.commentCount = getRandom(200);
            trend.isLiked = getRandom(2) == 1;
            int c = getRandom(9);
            if (c == 5 || c == 6){
                c = 1;
            }
            trend.imgs = new String[c];
            for(int j = 0 ; j < c; j ++){
                trend.imgs[j] = images[getRandom(6)];
            }
            trends.add(trend);
        }
        return trends;
    }

    public static List<Common.BriefUser> getBriefUsers(int count){
        List<Common.BriefUser> briefUsers = new ArrayList<>();
        for(int i = 0; i < count; i++){
            briefUsers.add(getBriefUser());
        }
        return briefUsers;
    }

    public static Common.BriefUser getBriefUser(){
        String[] avatars = new String[]{
                "http://www.th7.cn/d/file/p/2016/04/21/edabefd045e8d58ec90675a772341049.jpg",
                "http://www.poluoluo.com/qq/UploadFiles_7828/201604/2016042118324667.jpg",
                "http://hdn.xnimg.cn/photos/hdn321/20130612/2235/h_main_NNN4_e80a000007df111a.jpg",
                "http://avatar.csdn.net/E/F/3/1_u012131702.jpg"
        };
        Common.BriefUser briefUser = new Common.BriefUser();
        briefUser.userId = 0;
        briefUser.userName = "我就要上天";
        briefUser.userAvatar = avatars[getRandom(4)];
        return briefUser;
    }

    public static List<Common.CommentMessage> getCommentMessage(int count){
        List<Common.CommentMessage> messages = new ArrayList<>();
        String[] content = new String[]{"小夜色 评论了您的动态 “今天天气不错...” “要不要一起出来玩呀”"
                ,"ashdkahsd 评论了您的回复 “要不要一起出来玩呀” “今天肚子疼，就不去了，你们好好玩吧”"};
        String[] avatars = new String[]{
                "http://www.th7.cn/d/file/p/2016/04/21/edabefd045e8d58ec90675a772341049.jpg",
                "http://www.poluoluo.com/qq/UploadFiles_7828/201604/2016042118324667.jpg",
                "http://hdn.xnimg.cn/photos/hdn321/20130612/2235/h_main_NNN4_e80a000007df111a.jpg",
                "http://avatar.csdn.net/E/F/3/1_u012131702.jpg"
        };
        for (int i = 0; i < count ; i ++){
            Common.CommentMessage message = new Common.CommentMessage();
            message.messageId = 0;
            message.messageContent = content[getRandom(2)];
            message.createTime = System.currentTimeMillis() - getRandom(10) * 100000;
            message.avatar = avatars[getRandom(4)];
            message.trendId = 0;
            messages.add(message);
        }
        return messages;
    }

    private static int getRandom(int range){
        return (int) (Math.random() * range);
    }
}
