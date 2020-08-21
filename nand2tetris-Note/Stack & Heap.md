# 栈（Stack）和堆（Heap）

​		目前来说我们知道怎么去声明一个基础类型的变量，比如int，float，等等。以及复杂数据类型数组和结构，声明它们的时候C会把这些变量放在栈上。**每个线程都有一个栈，而程序通常只有一个堆**。

### 栈（Stack）

  什么是栈？它是内存中一块特殊的区域，**用于保存在函数中声明的零时变量**（其中也包括`main()`函数）。栈是LIFO(Last in First Out 后进先出)的数据结构，进出操作是由CPU来管理和优化的。**调用函数时就会使用栈。**每当函数声明了一个变量，该变量就会被**推入(Pushed)**栈中。每当函数退出时，所有的变量都会被函数推出栈，并被**释放掉(Free)**。一旦变量被释放，该内存区域就可以被其他栈变量使用。
  使用栈的优势是它会为你管理内存，而不需要你手动去分配或者释放内存。更进一步说，由于CPU可以有效地管理栈内存，所以从栈中读写变量是很快的。
  理解栈的关键是需要知道函数什么时候退出，此时栈中所有的变量被推出，因此**栈变量是局部的**（也就是局部变量）。C中经常出现的一个错误就是，在函数返回以后去访问函数内部中栈变量。
  如果使用太多的栈空间会导致溢出，比如在使用递归的时候，该函数使用了太多的局部变量在递归过程中就有可能造成**栈溢出**。

> 总结
>
> - 栈是LIFO数据结构；
> - CPU管理内存，而不需要手动去管理。正是因为这个原因从栈中读写变量**很快**的；
> - 栈变量是局部的（也就是局部变量）；
> - 栈的容量会随着函数的Push和Pop变化；

### 堆（Heap）

  堆也是内存中一块特定区域，但是CPU并不会自动管理相关的操作，而且它的空间大小会有一定的浮动。在堆上分配内存的时候，在C中使用`malloc()`和`calloc()`函数。在不需要堆上这块内存之后，需要使用`free()`函数释放掉它。**如果不释放的话就会造成内存泄漏**，这块内存就会被闲置。
  和栈不同之处在于，**堆内存数据的读写速度会比栈慢**。

### 栈（Stack）和堆（Heap）的差异

#### 配置堆栈大小

  堆的大小在程序启动时分配，数值在不同操作系统中可能有所不同。
  在Cocoa中想要修改线程的栈大小的话，可以使用`NSThread`的实例方法`setStackSize:`，如果使用POSIX线程技术创建的线程的话，想要设置栈大小的话使用`pthread_attr_setstacksize`函数。

> ⚠️如果要设置栈大小就必须要在创建线程之前完成。

```c++
// 第一种
NSThread *thread = [[NSThread alloc] initWithTarget:self selector:@selector(backgroudMethod:) object:nil];
[thread setStackSize:1024];
[thread start];

/// 第二种
pthread_attr_t attr;
pthread_t posix_tread_id;
int returnVal;
returnVal = pthread_attr_init(&attr);
returnVal = pthread_attr_setstacksize(&attr, 1024);  // 在创建线程前设置堆栈大小
char *data;
data = "To ensure that a thread knows what work to do";
int thread_error = pthread_create(&posix_tread_id, &attr, posix_thread_mainroutine, data);
```

#### 生命周期

  栈是和线程相关联的，意思就是说当线程退出时，栈被回收。而堆通常是在启动程序时分配，当程序退出之后被回收。

### 什么时候使用栈什么时候使用堆

- 需要申请较大内存空间（比如struct，array之类的），而且需要该变量存在较长时间，就是将该变量放在**堆**中；
- 如果需要动态修改struct或者array的大小，将该变量放在**堆**上。使用`malloc()，calloc()，realloc()和free()`等函数来管理内存；
- 如果使用相对较小的变量，并且只在函数中使用它们，此时该变量就存在于栈上。这样做会更快而且更简单。

