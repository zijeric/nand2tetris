void myPrintf(char* string, ...);

main() {
    myPrintf("%d %c abc %%\n", 684, 0x61);
}

void myPrintf(char* string, ...) 
{
    int stackIndex = 0;
    int stringIndex = 0;
    int screenIndex = 40;
    while(string[stringIndex] != 0) 
    {
        if(string[stringIndex] == '%') 
        {
            if(string[stringIndex+1] == 'c') 
            {
                *(char far *)(0xb8000000+160*14+screenIndex) = *(char*)(_BP+6+stackIndex);
                stackIndex += 2;
                stringIndex += 2;
                screenIndex += 2;
            }
            else if(string[stringIndex+1] == 'd')
            {
                *(char far *)(0xb8000000+160*14+screenIndex) = *(int*)(_BP+6+stackIndex) + 0x30;
                stackIndex += 2;
                stringIndex += 2;
                screenIndex += 2;
            }
        }
        else{
            *(char far*)(0xb8000000+160*14+screenIndex) = string[stringIndex];
            screenIndex += 2;
            stringIndex += 1;
        }                
    }
}