#include "Thread.h"

List *Thread_Set = List_new(BStar_equal);
List *PrivilegedThread_Set = List_new(BStar_equal);
static proposition _T_Scheduler_cpuId_equal (T_Scheduler *o1, T_Scheduler *o2){
return o1==o2||o1->cpuId == o2->cpuId;
}
static proposition _T_Partition_parID_equal (T_Partition *o1, T_Partition *o2){
return o1==o2||o1->parID == o2->parID;
}
static proposition _T_KTCB_tid_equal (T_KTCB *o1, T_KTCB *o2){
return o1==o2||o1->tid == o2->tid;
}
static proposition _T_AddressSpace_spid_equal (T_AddressSpace *o1, T_AddressSpace *o2){
return o1==o2||o1->spid == o2->spid;
}

void InstallThrRegsCtxt(T_KTCB *thr)
{
return;
}
void ktcbInit(T_KTCB *newThread, T_KTCB *pager, INT32 priority, T_TaskName name, errorCode *RETURN_CODE)
{
INT32 task_id = allocResourceId(kernelResources->ktcbIds)if (task_id==NULL){
*RETURN_CODE = ENOMEM;
return;
}
newThread->tid = task_id;
T_Scheduler _Scheduler = {};
T_Scheduler *Scheduler;
_Scheduler.cpuId = getCpuId();
Scheduler = List_get2(&Scheduler_Set, &_Scheduler, _T_Scheduler_cpuId_equal);
newThread->spid = Scheduler->idleTask->spid;
newThread->tgid = Scheduler->idleTask->tgid;
newThread->thrState = TASK_INACTIVE;
newThread->thrTransFlag = newThread->thrTransFlag|TASK_RESUMING;
newThread->priority = priority;
newThread->thrTickLeft = MAX_THREAD_TICKS;
newThread->thrPagerID = pager->tid;
kernelMutexInit(&(newThread->thrCtrLock), RETURN_CODE);
if (!(ASSERT(*RETURN_CODE))){
return;
}
newThread->threadLock.lock = 1;
newThread->waitLock.lock = 1;
waitThreadSetInfoInit(newThread->RecWaitSetInfo);
waitThreadSetInfoInit(newThread->SendWaitSetInfo);
waitThreadSetInfoInit(newThread->NotifySetInfo);
waitThreadSetInfoInit(newThread->WaitPagerSetInfo);
waitThreadSetInfoInit(newThread->WaitingOnSetInfo);
thrCapSetInfoInit(newThread->thrCapSetInfo);
newThread->name = name;
*RETURN_CODE = OK;
return;
}
void getKtcbIds(T_KTCB *thr, T_TaskIds *ids)
{
ids->tid = thr->tid;
ids->tgid = thr->tgid;
ids->spid = thr->spid;
return;
}
void threadCreate(T_TaskIds *ids, INT32 priority, T_TaskName name, spaceSetUpAttr spaceAtt, T_TaskIds *newIds, errorCode *RETURN_CODE)
{
T_KTCB *curThread, *newThread;
curThread.ChildThread_Set = List_new(BStar_equal);
curThread.Robin_Set = List_new(BStar_equal);
curThread.ThrCap_Set = List_new(BStar_equal);
curThread.Rec_WaitSet = List_new(BStar_equal);
curThread.Send_WaitSet = List_new(BStar_equal);
curThread.Notify_Set = List_new(BStar_equal);
curThread.WaitPager_Set = List_new(BStar_equal);
curThread.WaitingOn_Set = List_new(BStar_equal);
curThread.name = Array_new(256);
curThread.extendedIpcBuffer_Set = Array_new(256);
newThread.ChildThread_Set = List_new(BStar_equal);
newThread.Robin_Set = List_new(BStar_equal);
newThread.ThrCap_Set = List_new(BStar_equal);
newThread.Rec_WaitSet = List_new(BStar_equal);
newThread.Send_WaitSet = List_new(BStar_equal);
newThread.Notify_Set = List_new(BStar_equal);
newThread.WaitPager_Set = List_new(BStar_equal);
newThread.WaitingOn_Set = List_new(BStar_equal);
newThread.name = Array_new(256);
newThread.extendedIpcBuffer_Set = Array_new(256);
pageAttr *pAttr;
curThread = getCurThread();
T_Partition _curPar = {};
T_Partition *curPar;
_curPar.parID = curThread->parID;
curPar = List_get2(&Partition_Set, &_curPar, _T_Partition_parID_equal);
T_KTCB _any = {};
T_KTCB *any;
_any.tid = curThread->tid;
if (!((any = List_get2(&curPar->Pager_Set, &_any, _T_KTCB_tid_equal)) != null)){
*RETURN_CODE = ENOCAP;
return;
}
T_AddressSpace _curThrSpace = {};
T_AddressSpace *curThrSpace;
_curThrSpace.spid = ids->spid;
curThrSpace = List_get2(&curPar->AddressSpace_Set, &_curThrSpace, _T_AddressSpace_spid_equal);
pageAttributeGet(ids, curThrSpace->pgd, pAttr, RETURN_CODE);
if (!(ASSERT(*RETURN_CODE))){
return;
}
if (*pAttr!=MAP_USR_RW){
*RETURN_CODE = EINTR;
return;
}
capCheck(curThread->ThrCap_Set, CAP_TYPE_TCTRL, CAP_TCTRL_CREATE, RETURN_CODE);
if (!(ASSERT(*RETURN_CODE))){
*RETURN_CODE = ENOCAP;
return;
}
if (spaceAtt!=TC_SHARE_SPACE&&spaceAtt!=TC_COPY_SPACE&&spaceAtt!=TC_NEW_SPACE){
*RETURN_CODE = EINVAL;
return;
}
if (!(VALID(priority, 0, MAX_PRIO_VALUE))){
*RETURN_CODE = EINVAL;
return;
}
newThread = allocMemCache(kernelResources->pKtcbCache);
if (newThread==NULL){
*RETURN_CODE = ENOMEM;
return;
}
List *bStarTemp = List_new(BStar_equal);
List_add(&bStarTemp, &newThread);
List_add_List(&Thread_Set, &bStarTemp);
Thread_Set = Thread_Set;
ktcbInit(newThread, curThread, priority, name, RETURN_CODE);
if (!(ASSERT(*RETURN_CODE))){
return;
}
if (*RETURN_CODE!=OK){
return;
}
InstallThrRegsCtxt(newThread);
List *bStarTemp0 = List_new(BStar_equal);
List_add(&bStarTemp0, &newThread);
List_add_List(&(curPar->ParThread_Set), &bStarTemp0);
curPar->ParThread_Set = (curPar->ParThread_Set);
*RETURN_CODE = OK;
getKtcbIds(newThread, newIds);
return;
}
