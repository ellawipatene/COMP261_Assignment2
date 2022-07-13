package comp261.assig2;

import java.util.Comparator;

public class PathCostComparator implements Comparator<PathItem> {
    @Override
    public int compare(PathItem x, PathItem y) {
        // if either is null then the real node is larger.
        if (x==null && y==null) { //null equals null
            return 0;
        }
        if (x==null) {
            return -1;
        }
        if (y==null) {
            return 1;
        }
        if (x.f < y.f) {
            return -1;  
        }
        if (x.f > y.f) {
            return 1;
        }
       return 0;
    }
}