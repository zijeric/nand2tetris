void myPrintf(char* string, ...);

main() {
    myPrintf("%d %c abc %%%%%%", 653, 0x61);
}

void myPrintf(char* string, ...) 
{
    int stackIndex = 0;
    int stringIndex = 0;
    int screenIndex = 40;
    while(string[stringIndex] != '\0') 
    {
        if(string[stringIndex] == '%') 
        {
            if(string[stringIndex + 1] == 'c' || string[stringIndex + 1] == 'd')
            {
                stringIndex++;
                if(string[stringIndex] == 'c') 
                {
                    *(char far *)(0xb8000000+160*14+screenIndex) = *(char*)(_BP+6+stackIndex);
                    stackIndex += 2;
                    stringIndex++;
                }
                else if(string[stringIndex] == 'd')
                {
                    int number = *(int *)(_BP+6+stackIndex);
                    int length = 0;
                    char temp[15];
                    while (number != 0){
                        temp[length] = (number%10) + 0x30;
                        length++;
                        number/=10;
                    }
                    length--;
                    while (length >= 0){
                        *(char far *)(0xb8000000+160*14+screenIndex) = temp[length];
                        length--;
                        screenIndex += 2;
                    }
                    stackIndex += 2;
                    stringIndex++;
                    screenIndex -= 2;
                }
            }
            else{
                *(char far*)(0xb8000000+160*14+screenIndex) = string[stringIndex];
                stringIndex++;
            }
        }else{
            *(char far*)(0xb8000000+160*14+screenIndex) = string[stringIndex];
            stringIndex++;
        }
        screenIndex += 2;
    }
}