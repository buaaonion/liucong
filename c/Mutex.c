#include "Mutex.h"

List *MutexLock_Set = List_new(BStar_equal);

void kernelMutexInit(T_Mutex *MLock, errorCode *RETURN_CODE)
{
MLock->WaitThread_Set = NULL;
MLock->lock = MUTEX_UNLOCKED;
MLock->WaitThreadSetInfo->sleepers = 0;
MLock->WaitThreadSetInfo->slock.lock = MUTEX_UNLOCKED;
MLock->WaitThreadSetInfo->taskList = NULL;
List *bStarTemp = List_new(BStar_equal);
List_add(&bStarTemp, &MLock);
List_add_List(&MutexLock_Set, &bStarTemp);
MutexLock_Set = MutexLock_Set;
*RETURN_CODE = OK;
return;
}
