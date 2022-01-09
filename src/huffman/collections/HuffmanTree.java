package huffman.collections;

import huffman.types.ImmutableByteArray;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public class HuffmanTree {
    public final HashMap<ImmutableByteArray, String> literalEncodings;
    public HuffmanNode root;

    public HuffmanTree(HashMap<ImmutableByteArray, Integer> freqTable) {
        literalEncodings = new HashMap<>(freqTable.size());
        var pq = new PriorityBlockingQueue<>(freqTable.size());
        freqTable.entrySet().parallelStream().forEach(e -> {
            pq.add(new HuffmanNode(e.getKey(), e.getValue()));
        });

        while (pq.size() > 1) {
            HuffmanNode left = (HuffmanNode) pq.poll();
            HuffmanNode right = (HuffmanNode) pq.poll();

            assert right != null;
            HuffmanNode f = new HuffmanNode(left.getFreq() + right.getFreq());
            f.left = left;
            f.right = right;
            root = f;
            pq.add(f);
        }
    }


    public HuffmanTree(Map.Entry<ImmutableByteArray, Integer>[] sortedList) {
        literalEncodings = new HashMap<>(sortedList.length);
        Queue<HuffmanNode> treeNodes = new PriorityQueue<>();
        int[] nodesQ = new int[]{0};
        while (treeNodes.size() > 1 || nodesQ[0] < sortedList.length - 2) {
            HuffmanNode n1, n2, parent;
            n1 = minDeque(sortedList,nodesQ,treeNodes);
            n2 = minDeque(sortedList,nodesQ,treeNodes);

            parent = new HuffmanNode(n1.getFreq() + n2.getFreq());
            parent.left = n1;
            parent.right = n2;
            treeNodes.add(parent);
        }
        this.root = treeNodes.poll();
    }

    private HuffmanNode  minDeque(Map.Entry<ImmutableByteArray, Integer>[]  arr,
                                  int[] index, Queue<HuffmanNode> q){
        if (index[0] == arr.length )
            return q.poll();

        if (q.isEmpty()){
            return new HuffmanNode(arr[index[0]].getKey(),arr[index[0]++].getValue());
        }

        if (arr[index[0]].getValue() < q.peek().getFreq())
            return new HuffmanNode(arr[index[0]].getKey(),arr[index[0]++].getValue());

        return q.poll();
    }

    public static String Stack2Str(Stack<Character> s) {
        var rev = Arrays.asList(s.toArray());
        return rev.stream()
                .map(e -> e.toString())
                .reduce((acc, e) -> acc + e)
                .get();
    }

    public void flatten() {
        if(this.literalEncodings.size()  != 0 )
            return;
        Stack<HuffmanNode> s   = new Stack<>();
        Stack<Character> c     = new Stack<>();
        HashSet<HuffmanNode> v = new HashSet<>();
        HuffmanNode top = root;

        while (!top.isLeaf() || !s.empty()) {
            if (!top.isLeaf() && !v.contains(top)) {
                s.push(top);
                c.push('0');
                top = top.left;
            } else {
                if (top.isLeaf())
                    literalEncodings.put(top.getLiteral(), Stack2Str(c));
                if (!s.isEmpty()) {
                    top = s.peek();
                    if (v.contains(top)) {
                        s.pop();
                        c.pop();
                    } else {
                        v.add(top);
                        top = top.right;
                        c.pop();
                        c.push('1');
                    }
                } else {
                    break;
                }
            }
        }
    }

    public void printCode(HuffmanNode root, String s) {
        if (root.isLeaf()) {
            literalEncodings.put(root.getLiteral(), s);
            return;
        }
        printCode(root.left, s + "0");
        printCode(root.right, s + "1");
    }
}