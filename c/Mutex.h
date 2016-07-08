#ifndef _Mutex_
#define _Mutex_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "ThreadDecls.h"
#define MUTEX_UNLOCKED 0
#define MUTEX_LOCKED 1
typedef enum {
WAKEUP_INTERRUPT = 1,
WAKEUP_SYNC = 2,
WAKEUP_ASYNC = 4
} T_WakeupFlags;
extern List *MutexLock_Set;
extern void kernelMutexInit(T_Mutex *MLock, errorCode *RETURN_CODE);
