package pipeline.shuffling;

/**
 * Represents a single entry in a linkage disequilibrium (LD) dataset.
 * Stores the positions of two genetic markers (base pairs) and their r² value.
 */
public class LdEntry {

    /** The position of the first genetic marker (base pair). */
    int bpA;

    /** The position of the second genetic marker (base pair). */
    int bpB;

    /** The linkage disequilibrium r² value between the two markers. */
    double r2;

    /**
     * Constructs a new LdEntry with the specified positions and r² value.
     *
     * @param bpA the position of the first marker
     * @param bpB the position of the second marker
     * @param r2 the LD r² value between the two markers
     */
    LdEntry(int bpA, int bpB, double r2) {
        this.bpA = bpA;
        this.bpB = bpB;
         this.r2 = r2;
    }
}
