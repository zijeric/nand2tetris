assume cs:code,ss:stacksg

stacksg segment
    db 128 dup (0)
stacksg ends
code segment
start:
    mov ax,stacksg
    mov ss,ax
    mov sp,128

    call COPY_BOOT
    ; call save_old_int9

    ; 通过栈式操作来设置CS:IP为0:7E00H
    mov ax,0
    push ax
    mov ax,7E00H
    push ax
    retf

    mov ax,4C00H
    int 21H

;========================================================
; 引导程序
BOOT:
    jmp BOOT_START
OPTION_1        db  '1) reset PC',0
OPTION_2        db  '2) start system',0
OPTION_3        db  '3) show clock',0
OPTION_4        db  '4) set clock',0
; 详见之后得到标号的
OPTION_ADDRESS  dw  offset OPTION_1 - offset BOOT + 7E00H
                dw  offset OPTION_2 - offset BOOT + 7E00H
                dw  offset OPTION_3 - offset BOOT + 7E00H
                dw  offset OPTION_4 - offset BOOT + 7E00H

TIME_STYLE      db  'YY/MM/DD hh:mm:ss',0
CMOS_ADDRESS    db  9,8,7,4,2,0
SHOW_CLOCK      db  'F1----change the color        ESC----return menu',0
SET_CLOCK       db  'Please input Date and Time,(YY MM DD hh mm ss):',0

STRING_STACK    db  12 dup (0),0

BOOT_START:
    call INIT_REGISTER
    call CLS_SCREEN
    call SHOW_SCREEN
    jmp CHOOSE
    mov ax,4C00H
    int 21H

CHOOSE:
    call CLEAR_KB_BUFFER

BOOT_END:
    nop

COPY_BOOT:
    mov bx,cs
    mov ds,bx
    mov si,offset BOOT

    mov bx,0
    mov es,bx
    mov byte ptr di,7E00H

    mov cx,offset BOOT_END-offset BOOT
    cld
    rep movsb
    ret
