package huffman.collections;

import huffman.types.ImmutableByteArray;

public class HuffmanNode implements Comparable<HuffmanNode> {
    private final ImmutableByteArray literal;
    private int freq;
    protected HuffmanNode left;
    protected HuffmanNode right;

    public HuffmanNode( int freq) {
        this.freq    = freq;
        this.literal = null;
    }

    public HuffmanNode(ImmutableByteArray value, int freq) {
        this.literal = value;
        this.freq = freq;
    }
    public void setFreq(int freq){
        this.freq = freq;
    }
    public int getFreq(){
        return freq;
    }

    public ImmutableByteArray getLiteral() {
        return literal;
    }

    public boolean isLeaf(){
        return left == null && right == null;
    }


    @Override
    public int compareTo(HuffmanNode o) {
        return Integer.compare(this.freq,o.freq);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(" ");
        sb.append("{Literal: ");
        if(this.literal != null)
            sb.append(literal);
        else
            sb.append("null}");
        return sb.toString();
    }
}
