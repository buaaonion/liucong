#ifndef _Partition_
#define _Partition_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "ThreadDecls.h"
#include "Space.h"
typedef enum {
PAR_IDLE = 1,
PAR_NORMAL = 2,
PAR_STOP = 4
} partStatus;
typedef struct T_Partition{
P_ID parID;
INT32 pagerCounts;
T_Link parLink;
UINT32 parStatus;
List AddressSpace_Set;
T_ParName name;
List ParThread_Set;
List Pager_Set;
List Interrupt_Set;
List Exception_Set;
T_MutexQueueHead *MutexManagerSetInfo;
List MutexManager_Set;
List ParCap_Set;
List Boot_Pager_Set;
};
typedef T_Partition T_PTCB;
extern List *Partition_Set;
