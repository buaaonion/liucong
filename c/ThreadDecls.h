#ifndef _ThreadDecls_
#define _ThreadDecls_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "Mem.h"
#include "Capability.h"
typedef T_Link T_RunQueue;
typedef enum {
FIND_IDS_BY_SELF,
FIND_IDS_BY_NAME
} findType;
typedef struct T_WaitQueue{
T_ID wait_tid;
UINT32 wait_type;
};
typedef enum {
WAIT_PAGER = 1,
IPC_SEND = 2,
IPC_REC = 4,
NOTIFY = 8,
WAITING = 16
} Waiting_Type;
typedef enum {
TASK_INACTIVE = 0,
TASK_SLEEPING = 1,
TASK_RUNNABLE = 2
} T_TaskState;
typedef enum {
TASK_INTERRUPTED = 1,
TASK_SUSPENDING = 2,
TASK_RESUMING = 4,
TASK_EXITED = 8
} tTransFlag;
typedef struct Task_Notify_Slot{
INT32 taskNotifySlot;
UINT32 notify;
};
typedef struct T_UTCB{
UINT32 cpuId;
Array Mr_Set;
UINT32 savedTag;
UINT32 savedSender;
Array Notify_Set;
Array MrRest_Set;
};
typedef struct T_ExregsData{
T_ExregsContext context;
UINT32 validVect;
UINT32 flags;
T_ID pagerid;
T_UTCB *utcbAddr;
};
typedef enum {
EXREGS_READ = 1,
EXREGS_WRITE = 2
} T_ContextOpAttr;
typedef enum {
EXREGS_REGS = 4,
EXREGS_UTCB = 8,
EXREGS_PAGER = 16
} T_ContextRangeAttr;
typedef T_ExregsContext T_SyscallContext;
typedef enum {
NO_TARGET_NORMAL = 0xffff0000,
NO_TARGET_YIELD = 0xffff0001
} T_SpcThrSwitchAttr;
typedef T_Mutex T_UserMutex;
typedef struct T_Thread{
T_TaskIds ids;
T_UserMutex lock;
T_Link userThrlist;
ULONG *pUsrStack;
T_UTCB *utcb;
};
extern T_Thread **ppUserThread;
typedef struct T_KTCB *ktcb;
typedef struct T_KTCB{
List ChildThread_Set;
T_TaskContext context;
T_SyscallContext *syscallRegs;
T_Link robinLink;
List Robin_Set;
T_Link rqList;
T_RunQueue *rq;
T_ID tid;
T_ID tgid;
INT32 affinity;
T_ID thrPagerID;
UINT32 thrTransFlag;
UINT32 ipcFlags;
T_Mutex thrCtrLock;
T_SpinLock threadLock;
UINT32 tsNeedResched;
UINT32 tsNeedRobin;
T_TaskState thrState;
T_Link parThrLink;
T_Link taskList;
T_UTCB *utcbAddress;
UINT32 kernelTime;
UINT32 sysTime;
UINT32 thrTickLeft;
UINT32 ticksAssigned;
INT32 priority;
INT32 queueState;
INT32 nlocks;
UINT32 exitCode;
T_ID spid;
T_ID parID;
ktcb pager;
T_CapList *thrCapSetInfo;
List ThrCap_Set;
T_WaitQueueHead *RecWaitSetInfo;
List Rec_WaitSet;
T_WaitQueueHead *SendWaitSetInfo;
List Send_WaitSet;
T_ID expectedSender;
T_WaitQueueHead *NotifySetInfo;
List Notify_Set;
T_WaitQueueHead *WaitPagerSetInfo;
List WaitPager_Set;
T_SpinLock waitLock;
T_WaitQueueHead *WaitingOnSetInfo;
List WaitingOn_Set;
T_WaitQueue *wq;
INT32 wdControl;
int *threadEntry;
UINT32 startArg;
T_TaskName name;
ULONG extendedIpcSize;
Array extendedIpcBuffer_Set;
};
