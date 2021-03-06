package org.woehlke.beachbox.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.woehlke.beachbox.entities.Vinyl;
import org.woehlke.beachbox.entities.VinylType;
import org.woehlke.beachbox.service.VinylService;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * Created by Fert on 27.03.2014.
 */
@Controller
@SessionAttributes("searchItem")
public class VinylController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VinylController.class);

    @Inject
    private VinylService vinylService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getAll(@PageableDefaults(pageNumber = 0, value = 30, sort={"interpret"}) Pageable pageable, Model model){
        Page<Vinyl> page;
        SessionBean searchItem;
        pageable = addSort(pageable);
        if(model.containsAttribute("searchItem")) {
            searchItem = (SessionBean) model.asMap().get("searchItem");
        } else {
            searchItem = new SessionBean();
        }
        if(!searchItem.isEmpty()) {
            page = vinylService.search(searchItem.getSearchString(),pageable);
        } else {
            page = vinylService.findAll(pageable);
        }
        searchItem.setPageSize(pageable.getPageSize());
        int current = page.getNumber() + 1;
        int begin = Math.max(1, current - 10);
        int end = Math.min(begin + 19, page.getTotalPages());
        Sort mySort = page.getSort();
        String sort = "";
        if(mySort != null) {
            sort = page.getSort().toString().split(":")[0];
            //LOGGER.info("sort: " + sort);
        }
        model.addAttribute("page",page);
        searchItem.setBeginIndex(begin);
        searchItem.setEndIndex(end);
        searchItem.setCurrentIndex(current);
        searchItem.setSort(sort);
        searchItem.setSortDir("asc");
        model.addAttribute("searchItem",searchItem);
        return "all";
    }

    private Pageable addSort(Pageable pageable){
        Sort sort = pageable.getSort();
        Sort.Order order = sort.iterator().next();
        String property = order.getProperty();
        Sort.Direction direction = order.getDirection();
        if(property.equals("song")){
            pageable = new PageRequest(pageable.getPageNumber(),pageable.getPageSize(), direction,"song","interpret");
        } else if (property.equals("interpret")) {
            pageable = new PageRequest(pageable.getPageNumber(),pageable.getPageSize(), direction,"interpret","song");
        } else {
            pageable = new PageRequest(pageable.getPageNumber(),pageable.getPageSize(), direction ,property, "interpret","song");
        }
        return pageable;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String search(@Valid SessionBean searchItem, BindingResult result,
                         @PageableDefaults(pageNumber = 0, value = 30,  sort={"interpret"}) Pageable pageable, Model model){
        Page<Vinyl> page;
        pageable = addSort(pageable);
        if (result.hasErrors() || searchItem.isEmpty()){
            page = vinylService.findAll(pageable);
        } else {
            page = vinylService.search(searchItem.getSearchString(),pageable);
        }
        int current = page.getNumber() + 1;
        int begin = Math.max(1, current - 10);
        int end = Math.min(begin + 19, page.getTotalPages());
        Sort mySort = page.getSort();
        String sort = "";
        if(mySort != null) {
            sort = page.getSort().toString().split(":")[0];
            //LOGGER.info("sort: " + sort);
        }
        model.addAttribute("page", page);
        searchItem.setBeginIndex(begin);
        searchItem.setEndIndex(end);
        searchItem.setCurrentIndex(current);
        searchItem.setSort(sort);
        searchItem.setSortDir("asc");
        model.addAttribute("searchItem",searchItem);
        model.addAttribute("type", VinylType.values());
        return "all";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editGet(@PathVariable long id, Model model){
        Vinyl vinyl = vinylService.findById(id);
        model.addAttribute("vinyl",vinyl);
        model.addAttribute("type", VinylType.values());
        return "edit";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String editPost(@PathVariable long id, @Valid Vinyl vinyl,Model model){
        vinyl = vinylService.save(vinyl);
        model.addAttribute("vinyl",vinyl);
        String pageInfo = "";
        if(model.containsAttribute("searchItem")) {
            SessionBean searchItem = (SessionBean) model.asMap().get("searchItem");
            pageInfo +=
                    "?page.page="+searchItem.getCurrentIndex()+
                    "&page.size="+searchItem.getPageSize()+
                    "&page.sort="+searchItem.getSort()+
                    "&page.sort.dir="+searchItem.getSortDir();
        }
        return "redirect:/"+pageInfo;
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newGet(Model model){
        Vinyl vinyl = new Vinyl();
        model.addAttribute("vinyl",vinyl);
        model.addAttribute("type", VinylType.values());
        return "new";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String newPost(@Valid Vinyl vinyl,Model model){
        vinyl = vinylService.save(vinyl);
        model.addAttribute("vinyl",vinyl);
        String pageInfo = "";
        if(model.containsAttribute("searchItem")) {
            SessionBean searchItem = (SessionBean) model.asMap().get("searchItem");
            pageInfo +=
                    "?page.page="+searchItem.getCurrentIndex()+
                            "&page.size="+searchItem.getPageSize()+
                            "&page.sort="+searchItem.getSort()+
                            "&page.sort.dir="+searchItem.getSortDir();
        }
        return "redirect:/"+pageInfo;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteGet(@PathVariable long id, Model model){
        Vinyl vinyl = vinylService.findById(id);
        model.addAttribute("vinyl",vinyl);
        model.addAttribute("type", VinylType.values());
        return "delete";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String deletePost(@PathVariable long id, Vinyl vinyl, Model model){
        vinylService.deleteById(id);
        String pageInfo = "";
        if(model.containsAttribute("searchItem")) {
            SessionBean searchItem = (SessionBean) model.asMap().get("searchItem");
            pageInfo +=
                    "?page.page="+searchItem.getCurrentIndex()+
                            "&page.size="+searchItem.getPageSize()+
                            "&page.sort="+searchItem.getSort()+
                            "&page.sort.dir="+searchItem.getSortDir();
        }
        return "redirect:/"+pageInfo;
    }

    @RequestMapping(value = "/unlockEdit", method = RequestMethod.GET)
    public String unlockEdit(Model model){
        SessionBean searchItem = (SessionBean) model.asMap().get("searchItem");
        searchItem.setBearbeiten(true);
        model.addAttribute("searchItem",searchItem);
        String pageInfo = "";
        if(model.containsAttribute("searchItem")) {
            pageInfo +=
                    "?page.page="+searchItem.getCurrentIndex()+
                            "&page.size="+searchItem.getPageSize()+
                            "&page.sort="+searchItem.getSort()+
                            "&page.sort.dir="+searchItem.getSortDir();
        }
        return "redirect:/"+pageInfo;
    }

    @RequestMapping(value = "/install", method = RequestMethod.GET)
    public String install(Model model){
        vinylService.installInitialData();
        return "redirect:/";
    }

    @RequestMapping(value = "/update01", method = RequestMethod.GET)
    public String update01(Model model){
        vinylService.update01();
        return "redirect:/";
    }
}
