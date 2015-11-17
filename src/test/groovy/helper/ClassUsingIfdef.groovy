package helper

import houtbecke.rs.grappium.Platforms;

class ClassUsingIfdef implements Platforms {

        boolean detectsIfdef() {
            boolean ret = false
            ifdefDetect {
                ret = it.equals("detected")
            }
            ret
        }

        boolean detectsIfnotdef() {
            boolean ret = false
            ifnotdefDetect {
                ret = true
            }
            ret
        }


}
