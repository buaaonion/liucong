#include "Capability.h"

static proposition _T_Capability_type_equal (T_Capability *o1, T_Capability *o2){
return o1==o2||o1->type == o2->type;
}

errorCode *capCheck(List capSet, capType cType, capAccess cAccess, errorCode *RETURN_CODE)
{
T_Capability _capObj = {};
T_Capability *capObj;
_capObj.type = cType;
if (!((capObj = List_get2(&capSet, &_capObj, _T_Capability_type_equal)) != null)){
*RETURN_CODE = ENOCAP;
return RETURN_CODE;
}
if (capObj->access&cAccess==0){
*RETURN_CODE = ENOCAP;
return RETURN_CODE;
}
*RETURN_CODE = OK;
return RETURN_CODE;
}
