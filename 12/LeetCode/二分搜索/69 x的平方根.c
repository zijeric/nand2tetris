/**
 * 实现 int sqrt(int x) 函数。
 * 计算并返回 x 的平方根，其中 x 是非负整数。
 * 由于返回类型是整数，结果只保留整数的部分，小数部分将被舍去。
 */
int mySqrt(int x){
    int left = 1;
    int right = (x>>1) + 1;
    while(right >= left) {
        int mid = left + ((right - left)>>1);
        if(x/mid < mid) {
            right = mid - 1; 
        }
        else if(x/mid > mid) {
            left = mid + 1;
        }
        else return mid;
    }
    // left or right
    return right;
}
/**
 * 整体思路：
 * 1.比较mid*mid跟x的大小，相等则直接返回mid，否则就去以mid为分割点的左右区间去查找，循环直到left > right退出
 * 2.x(x ≠ 0)的平方根一定是落在[1, x/2 + 1]区间，所以取 left = 1, right = x/2+1，而不取right = x
 * 3.为了防止 mid * mid 太大，取 mid 跟 x/mid 进行比较。
 */
