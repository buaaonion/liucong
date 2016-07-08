#include "Sched.h"

Thread_Info *CurThread_Info;
List *Scheduler_Set = List_new(BStar_equal);

T_KTCB *getCurThread()
{
return CurThread_Info->thr;
}
UINT32 getCpuId()
{
return CurThread_Info->cpuId;
}
UINT32 isPreemptable()
{
return CurThread_Info->preemptingCounter;
}
