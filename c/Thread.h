#ifndef _Thread_
#define _Thread_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "assert.h"
#include "arch_types.h"
#include "error_code.h"
#include "Mem.h"
#include "Mutex.h"
#include "Sched.h"
#include "SyscallDecls.h"
#include "Resource.h"
#include "Partition.h"
extern List *Thread_Set;
extern List *PrivilegedThread_Set;
extern void InstallThrRegsCtxt(T_KTCB *thr);
extern void ktcbInit(T_KTCB *newThread, T_KTCB *pager, INT32 priority, T_TaskName name, errorCode *RETURN_CODE);
extern void getKtcbIds(T_KTCB *thr, T_TaskIds *ids);
extern void threadCreate(T_TaskIds *ids, INT32 priority, T_TaskName name, spaceSetUpAttr spaceAtt, T_TaskIds *newIds, errorCode *RETURN_CODE);
