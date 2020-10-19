/** 
 * Forward declaration of guess API.
 * @param  num   your guess
 * @return 	     -1 if num is lower than the guess number
 *			      1 if num is higher than the guess number
 *               otherwise return 0
 * int guess(int num);
 * 
 * Tips：
 * 1 <= n <= 2^(31) - 1
 * 1 <= pick <= n
 */

int guessNumber(int n){
	int low = 1;
    int high = n;
    while (high >= low) {
        int mid = low + (high - low) / 2;
        int guessNum = guess(mid);
        if (guessNum == 0)
            return mid;
        else if (guessNum == -1)
            high = mid - 1;
        else
            low = mid + 1;
    }
    return -1;
}