#ifndef _Resource_
#define _Resource_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "ThreadDecls.h"
#include "error_code.h"
#define MAX_PAR_PAGER_COUNTS 256
#define MAX_PAR_THREAD_COUNTS 256
#define MAX_PAR_MUTEX_COUNTS 256
#define MAX_PARTITION_COUNTS 256
#define MAX_THREAD_COUNTS (256*256)
#define MAX_SPACE_COUNTS 256
#define MAX_CAP_COUNTS 256
#define MAX_PGD_COUNTS (1024*1024)
#define MAX_PMD_COUNTS (1024*1024)
#define SYSTEM_IDS_MAX (256*256/32)
typedef struct secBitmap{
INT32 bitmap_vector;
UINT32 bitmap_val;
};
typedef secBitmap idPoolSecBitmap;
typedef struct T_IdPool{
T_SpinLock idPoolLock;
INT32 bitMapCounts;
List idPoolSecBitmap_Set;
};
typedef secBitmap memCacheSecBitmap;
typedef struct T_MemCache{
T_Link list;
T_Mutex mutex;
INT32 total;
INT32 free;
UINT32 start;
UINT32 end;
UINT32 structSize;
List memCacheSecBitmap_Set;
};
typedef struct T_ContainerHead{
INT32 ncont;
T_Link list;
T_SpinLock lock;
};
typedef struct T_KernelResources{
P_ID cid;
T_IdPool *spaceIds;
T_IdPool *ktcbIds;
T_IdPool *resourceIds;
T_IdPool *containerIds;
T_IdPool *mutexIds;
T_IdPool *capabilityIds;
T_ContainerHead *containers;
T_CapList *physMemUsed;
T_CapList *physMemFree;
T_CapList *virtMemUsed;
T_CapList *virtMemFree;
T_CapList *devMemUsed;
T_CapList *devMemFree;
T_CapList *nonMemoryCaps;
T_MemCache *pPgdCache;
T_MemCache *pPmdCache;
T_MemCache *pKtcbCache;
T_MemCache *pSpaceCache;
T_MemCache *pMutexCache;
T_MemCache *pCapCache;
T_MemCache *pContCache;
};
extern T_KernelResources *kernelResources;
extern INT32 bitMapSearchSet(List secBitmap_Set, INT32 bitmapCounts);
extern void bitMapSearchClear(List secBitmap_Set, INT32 index, INT32 bitmapCounts, errorCode *RETURN_CODE);
extern INT32 allocResourceId(T_IdPool *resIds);
extern void *allocMemCache(T_MemCache *memCache);
extern void freeMemCache(T_MemCache *memCache, UINT32 addr, errorCode *RETURN_CODE);
