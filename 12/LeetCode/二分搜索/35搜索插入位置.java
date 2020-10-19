/**
* 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。
* 如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
* 你可以假设数组中无重复元素。
*/
public class Solution {

    // 二分搜索实现: 搜索序列化数组的插入位置
    public int searchInsert(int[] numbers, int target) {
        // 错误检查
        // 1.数组长度
        int length = numbers.length;

        if(length == 0) return 0;
        // 2.target为最大
        if(target > numbers[length-1]) return length;

        int left = 0;
        int right = length - 1;
        while(right > left) {
            // 此题无需判断left == right，我们需要找插入的下标
            // 而非找相同的数

            // 比(left+right)/2更好，防溢出
            int mid = left + (right - left)/2;
            // int mid = (left + right) >>> 1;  // JDK 的源码中 Arrays.binarySearch()

            if(target > numbers[mid]) {
                // target > [mid], range: [mid+1, right]
                left = mid + 1;
            } else {
                // target <= [mid], range: [left, mid]
                right = mid;
            }
        }
        // 返回下标，此时left=right
        return right;
    }
}
/* 整理思路：
 * 首先，错误检查
 * 1.数组长度;
 * 2.插入位置有可能在数组的末尾(题目中的示例 3)，需要单独判断;
 * 否则，插入位置的下标是大于等于target的第1个元素的位置
 * 左右区间赋值，注意:mid防溢出
 * while、if注意：>与mid+1, <=与mid
 */