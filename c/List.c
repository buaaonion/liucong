#include "List.h"

T_SpinLock threadLock;
T_WaitQueueHead MutexQueueHead;
T_WaitQueueHead wqhPager;
T_Mutex threadControlLock;
threadControlLock.WaitThread_Set = List_new(BStar_equal);
T_CapList capList;

void waitThreadSetInfoInit(T_WaitQueueHead *wqh)
{
wqh->sleepers = 0;
wqh->slock.lock = 1;
}
void thrCapSetInfoInit(T_CapList *thrCapSetInfo)
{
thrCapSetInfo->ktcbRefs = 0;
thrCapSetInfo->ncaps = 0;
}
