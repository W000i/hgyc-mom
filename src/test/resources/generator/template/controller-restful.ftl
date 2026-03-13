package ${basePackage}.web;

import ${basePackage}.model.${modelNameUpperCamel};
import ${basePackage}.model.${modelNameUpperCamel}Criteria;
import ${basePackage}.service.${modelNameUpperCamel}Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.deer.wms.common.core.result.Result;
import com.deer.wms.common.core.result.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import com.deer.wms.common.core.controller.BaseController;
import com.deer.wms.common.core.page.TableDataInfo;

import java.util.List;

/**
* Created by ${author} on ${date}.
*/
@Controller
@RequestMapping("${modelNameLowerCamel}")
public class ${modelNameUpperCamel}Controller  extends BaseController{

    private String prefix = "${modelNameLowerCamel}";

    @Autowired
    private ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;



    /**
    * 详情
    */
    @GetMapping("/detail")
    public String detail()
    {
        return prefix + "/detail";
    }

    @RequiresPermissions("${modelNameLowerCamel}:view")
    @GetMapping()
    public String ${modelNameLowerCamel}()
    {
        return prefix + "/${modelNameLowerCamel}";
    }

    /**
    * 修改
    */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
    ${modelNameUpperCamel} ${modelNameLowerCamel} = ${modelNameLowerCamel}Service.findById(id);
        mmap.put("${modelNameLowerCamel}", ${modelNameLowerCamel});
        return prefix + "/edit";
    }

    /**
    * 新增
    */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }


    @PostMapping
    @ResponseBody
    public Result add(@RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}) {
        ${modelNameLowerCamel}Service.save(${modelNameLowerCamel});
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable ${type} id) {
        ${modelNameLowerCamel}Service.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping
    @ResponseBody
    public Result update(@RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}) {
        ${modelNameLowerCamel}Service.update(${modelNameLowerCamel});
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Result detail(@PathVariable ${type} id) {
        ${modelNameUpperCamel} ${modelNameLowerCamel} = ${modelNameLowerCamel}Service.findById(id);
        return ResultGenerator.genSuccessResult(${modelNameLowerCamel});
    }

    @PostMapping("/list")
    @ResponseBody
    public  TableDataInfo list(${modelNameUpperCamel}Criteria criteria) {
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        List<${modelNameUpperCamel}> list = ${modelNameLowerCamel}Service.findAll();
        return getDataTable(list);
    }

}
