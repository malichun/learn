import java.util.Arrays;
import java.util.LinkedList;

public class Test3 {

    public static int[] getMaxWindow(int[] arr,int w){
        int[] res = new int[arr.length-w+1];
        if (arr == null || w < 1 || arr.length < w) {
            return null;
        }
        LinkedList<Integer> qmax = new LinkedList<Integer>();
        int index = 0;
        for(int i=0;i<arr.length;i++){
            while(!qmax.isEmpty()&&arr[qmax.peekLast()]<=arr[i]){
                qmax.pollLast(); // 移除列表最后一个元素,如果列表为空则返回null
            }
            qmax.add(i);

            //i-w i=3 w=0;i=4 w= 1 ;i=5 w2
            //小于三的范围就是小于窗口范围（过期）
            if(qmax.peekFirst()==i-w){
                qmax.pollFirst();
            }


            //先调整完窗口。窗口最左边的左边就是最小值的坐标
            if(i >= w-1){
                res[index++] = arr[qmax.peekFirst()];
            }



        }
        return res;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int[] arr = { 4, 3, 5, 4, 3, 3, 6, 7 };
        int w = 3;
        System.out.println(Arrays.toString(getMaxWindow(arr, w)));
    }

}