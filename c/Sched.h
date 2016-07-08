#ifndef _Sched_
#define _Sched_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "Bitmap.h"
#include "ThreadDecls.h"
#define LOCAL_SCHED_ENABLED 0x1
#define SCHED_RUN_IDLE 0x2
#define SECINDEX_SIZE 8
typedef struct Thread_Info{
T_KTCB *thr;
UINT32 cpuId;
UINT32 preemptingCounter;
};
extern Thread_Info *CurThread_Info;
typedef struct T_ZombieListHead{
INT32 ncont;
T_Link list;
T_SpinLock lock;
};
typedef struct robinList{
INT32 priority;
List Robin_Set;
};
typedef struct prioSecBitmap{
INT32 topindex;
UINT8 secindex;
};
typedef struct T_Scheduler{
UINT32 cpuId;
UINT32 flags;
T_KTCB *idleTask;
UINT32 prioBitmapIndex;
Array PrioSecBitmap_Set;
List prioQueue;
T_ZombieListHead ZombieSetInfo;
List Zombie_Set;
};
extern List *Scheduler_Set;
extern T_KTCB *getCurThread();
extern UINT32 getCpuId();
extern UINT32 isPreemptable();
