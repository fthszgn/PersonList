package com.example.newtab;

import java.util.Random;

public class Util {

    public static int getRenderImage() {
        final Random r = new Random();
        int[] image = {R.drawable.person1, R.drawable.person2, R.drawable.person3,
                R.drawable.person4, R.drawable.person5,
                R.drawable.person6, R.drawable.person7};
        int unknown = R.drawable.personunknown;
        int imageRandom;
        int randomNumber;
        randomNumber = r.nextInt(10);
        if (randomNumber < 7) {
            imageRandom = image[randomNumber];
        } else {
            imageRandom = unknown;
        }
        return imageRandom;
    }
}
