int searchInsert(int* nums, int numsSize, int target){
    // 1.错误检查
    if(numsSize == 0) return 0;
    if(target > *(nums+numsSize-1)) return numsSize;

    int left = 0;
    int right = numsSize - 1;
    while(right > left) {
        int mid = left + (right - left)/2;
        if(target > nums[mid]) {
            left = mid + 1;
        } else {
            right = mid;
        }
    }
    return right;
}