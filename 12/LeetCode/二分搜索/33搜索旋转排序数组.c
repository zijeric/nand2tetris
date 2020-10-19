/**
 * 给你一个升序排列的整数数组nums，和一个整数target
 * 假设按照升序排序的数组在预先未知的某个点上进行了旋转
 * (例如，数组 [0,1,2,4,5,6,7] 可能变为 [4,5,6,7,0,1,2])
 * 请你在数组中搜索target，如果数组中存在这个目标值，则返回它的索引，否则返回-1
 * 
 * Tips:
 *     1 <= nums.length <= 5000
 *     -10^4 <= nums[i] <= 10^4
 *     nums 中的每个值都 独一无二
 *     nums 肯定会在某个点上旋转
 *     -10^4 <= target <= 10^4
 */ 

int search(int* nums, int numsSize, int target){
    int low = 0;
    int high = numsSize - 1;
    while(low <= high) {
        int mid = low + (high + low) / 2;
        if(nums[mid] == target) {
            return mid;
        }

        // 先根据 nums[0] 与 target 的关系判断目标值是在左半段还是右半段
        if(target >= nums[0]) {
            // 目标值在左半段时，若 mid 在右半段，则将 mid 索引的值改成 inf
            if(nums[0] > nums[mid]) {
                nums[mid] = 999;
            }
        } else {
            // 目标值在右半段时，若 mid 在左半段，则将 mid 索引的值改成 -inf
            if(nums[mid] >= nums[0]) {
                nums[mid] = -999;
            }
        }
        
        if(nums[mid] < target) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
    }
    return -1;
}