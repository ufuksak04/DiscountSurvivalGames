package me.discountzen.discountSurvivalGames.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    private final static int CENTER_PX = 120;


    public static ArrayList<String> BoxedMessage(ArrayList<String> header, ArrayList<String> body, ArrayList<String> footer, String color) {
        ArrayList<String> result = new ArrayList<>();
        String strikeline = color + ChatColor.STRIKETHROUGH + "";
        for (int i = 0; i < 80; i++) {
            strikeline += " ";
        }
        result.add(strikeline);
        result.addAll(CenterStringArray(header));
        result.add("");
        result.addAll(CenterStringArray(body));
        result.add("");
        result.addAll(CenterStringArray(footer));
        result.add(strikeline);
        return result;
    }

    public static ArrayList<String> BoxedMessage(ArrayList<String> header, ArrayList<String> body, String color) {
        ArrayList<String> result = new ArrayList<>();
        String strikeline = color + ChatColor.STRIKETHROUGH + "";
        for (int i = 0; i < 80; i++) {
            strikeline += " ";
        }
        result.add(strikeline);
        result.addAll(header);
        result.add("");
        result.addAll(body);
        result.add(strikeline);
        return result;
    }

    public static void SendMessageArray(Player player, ArrayList<String> messages) {
        for (String m : messages) {
            player.sendMessage(m);
        }
    }

    private static ArrayList<String> CenterStringArray(ArrayList<String> messages) {
        ArrayList<String> result = new ArrayList<>();
        for (String m : messages) {
            result.add(CenterString(m));
        }
        return result;
    }


    public static String CenterString(String message){
        if(message == null || message.equals("")) return "";
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if(previousCode == true){
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString() + message;
    }

    public static String PrettyTime(float time) {
        String result = "";
        String minutes = "";
        String seconds = "";
        int mins =  (int) (time / 60F);
        int secs =  (int) (time % 60F);
        if (mins < 10) {
            minutes += (mins < 1) ? "00" : "0" + mins;
        } else { minutes += mins; }
        if (secs < 10) {
            seconds += (secs < 1) ? "00" : "0" + secs;
        } else { seconds += secs; }
        result += minutes + ":" + seconds;
        return result;
    }


    public static String TitleCase(String text) {
        String result = "";
        ArrayList<String> words = new ArrayList<>();
        words.addAll(List.of(text.split(" ")));
        ArrayList<String> formattedWords = new ArrayList<>();
        for (String w : words) { formattedWords.add(Character.toTitleCase(w.charAt(0)) +  w.toLowerCase().substring(1)); }
        result = String.join(" ", formattedWords);
        return result;
    }
}
