#ifndef _List_
#define _List_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "arch_types.h"
typedef struct T_Link *node;
typedef struct T_Link{
node next;
node prev;
};
typedef struct T_SpinLock{
UINT32 lock;
};
extern T_SpinLock threadLock;
typedef struct T_WaitQueueHead{
UINT32 sleepers;
T_SpinLock slock;
T_Link taskList;
};
extern T_WaitQueueHead MutexQueueHead;
extern T_WaitQueueHead wqhPager;
typedef struct T_Mutex{
T_WaitQueueHead *WaitThreadSetInfo;
List WaitThread_Set;
UINT32 lock;
};
extern T_Mutex threadControlLock;
typedef struct T_MutexQueueHead{
T_Link list;
T_Mutex mutexControlMutex;
INT32 count;
};
typedef struct T_AddressSpaceList{
T_Link list;
T_Mutex lock;
INT32 count;
};
typedef struct T_CapList{
INT32 ktcbRefs;
INT32 ncaps;
T_Link caps;
};
extern T_CapList capList;
typedef struct T_MutexQueue{
INT32 contenders;
ULONG physical;
T_Link list;
T_WaitQueueHead *WaitContenderSetInfo;
List WaitContender_Set;
T_WaitQueueHead *WaitHolderSetInfo;
List WaitHolder_Set;
};
typedef struct T_KtcbList{
T_Link list;
T_SpinLock listLock;
INT32 count;
};
typedef T_KtcbList T_Pager;
typedef T_KtcbList pager;
extern void waitThreadSetInfoInit(T_WaitQueueHead *wqh);
extern void thrCapSetInfoInit(T_CapList *thrCapSetInfo);
