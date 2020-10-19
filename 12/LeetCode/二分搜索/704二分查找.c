/**
 * 给定一个n个元素有序的(升序x整型数组nums)和一个目标值target，
 * 写一个函数搜索nums中的target，如果目标值存在返回下标，否则返回 -1。
 */ 

int search(int* nums, int numsSize, int target){

    // 无需判断数组长度
    // if(numsSize == 0) return 0;

    int left = 0;
    int right = numsSize - 1;
    while(right >= left) {
        int mid = left + (right - left) / 2;
        if(target == nums[mid]) {
            return mid;
        }
        else if (target > nums[mid]) {
            left = mid + 1;
        }
        else {
            right = mid - 1;
        }
    }
    // 跳出则数组中没有该数字
    return -1;
}