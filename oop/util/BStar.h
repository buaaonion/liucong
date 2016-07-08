#ifndef BSTAR_H_INCLUDED_
#define BSTAR_H_INCLUDED_
#include <string.h>
#include <stdlib.h>

typedef char proposition;
typedef char* string;

typedef void* BStar_Object;
typedef proposition (*BStar_Equal)(BStar_Object e1, BStar_Object e2);//这是定义一个函数指针，函数的返回值类型为proposition,函数包含两个参数e1和e2,
typedef int (*BStar_Compare)(BStar_Object e1, BStar_Object e2);      //可以这么来调用BStar_Equal myBs=(BStar_Equal)GetProcAddress(_Module,"equal")说明这个函数实在mudule中
                                                                     //然后就可以通过myBs(x1,x2)来调用_Module模块中的equal函数了。
#define false 0
#define true 1
#define _EQUAL(o1,o2) ((o1)==(o2))

extern proposition BStar_equal(BStar_Object e1, BStar_Object e2);

extern proposition BStar_compare(BStar_Object e1, BStar_Object e2);

//#ifndef NULL
//#ifdef __cplusplus
//#ifndef _WIN64
//#define NULL 0
//#else
//#define NULL 0LL
//#endif  /* W64 */
//#else
//#define NULL ((void *)0)
//#endif
//#endif

#endif
