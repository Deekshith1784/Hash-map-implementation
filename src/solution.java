import java.util.*;

public class solution{

    static HashMap<String,Integer> users = new HashMap<>();
    static HashMap<String,Integer> attempts = new HashMap<>();

    static boolean checkAvailability(String username) {

        attempts.put(username, attempts.getOrDefault(username,0)+1);

        return !users.containsKey(username);
    }

    static List<String> suggestAlternatives(String username){

        List<String> list = new ArrayList<>();

        list.add(username + "1");
        list.add(username + "2");
        list.add(username.replace("_","."));

        return list;
    }

    static String getMostAttempted(){

        int max = 0;
        String name = "";

        for(String key : attempts.keySet()){

            if(attempts.get(key) > max){
                max = attempts.get(key);
                name = key;
            }
        }

        return name + " (" + max + " attempts)";
    }

    public static void main(String[] args) {

        users.put("john_doe",1);
        users.put("admin",2);

        System.out.println(checkAvailability("john_doe"));
        System.out.println(checkAvailability("jane_smith"));

        System.out.println(suggestAlternatives("john_doe"));

        System.out.println(getMostAttempted());
    }
}