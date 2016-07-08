#ifndef _Irq_
#define _Irq_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "ThreadDecls.h"
#define MAX_IRQS (12+21)
typedef enum {
IRQ_CONTROL_REGISTER = 0,
IRQ_CONTROL_RELEASE = 1,
IRQ_CONTROL_WAIT = 2
} irqReqType;
typedef struct T_IrqTask{
INT32 taskNotifySlot;
T_KTCB *pTask;
T_Link taskList;
};
typedef struct T_IrqChipOps{
};
typedef struct T_IrqChip{
T_IrqName name;
INT32 level;
INT32 cascade;
INT32 start;
INT32 end;
void *data;
T_IrqChipOps ops;
};
typedef struct T_IrqNotifyq{
INT32 tasks;
T_Link taskList;
List IrqTask_Set;
T_WaitQueueHead *WaitIrqSetInfo;
List WaitIrq_Set;
};
typedef struct T_IrqDesc{
INT32 irq;
T_IrqName name;
INT32 data;
T_IrqChip *pChip;
T_IrqNotifyq notify_q;
void *handler;
};
extern Array *IrqDesc_Set;
