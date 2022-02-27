package hunkarada.soulary.common.will;

import java.util.ArrayList;

public class WillRegistration {
    public static ArrayList<String> formOfWillIds = new ArrayList<>();
    public static void addId(BasicWill will){
        formOfWillIds.add(will.id);
    }
}
