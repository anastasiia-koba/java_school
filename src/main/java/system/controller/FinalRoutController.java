package system.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import system.entity.FinalRout;
import system.service.api.FinalRoutService;
import system.service.api.RoutService;
import system.service.api.TrainService;

import javax.validation.Valid;
import java.time.LocalTime;
import java.util.*;

/**
 * Controller for {@link system.entity.FinalRout}'s pages.
 */
@Secured(value = {"ROLE_ADMIN"})
@Controller
@RequestMapping(value = "/admin/finalrouts")
public class FinalRoutController {

    @Autowired
    private FinalRoutService finalRoutService;

    @Autowired
    private TrainService trainService;

    @Autowired
    private RoutService routService;

    @GetMapping
    public String getAdminFinalRoutsPage(Model model) {
        model.addAttribute("finalRoutForm", new FinalRout());
        model.addAttribute("trains", trainService.findAll());

        model.addAttribute("routs", routService.findAllValid());

        model.addAttribute("startpage", 1);

        int endpage = (int)Math.ceil(((double)finalRoutService.findAll().size())/10);
        model.addAttribute("endpage", endpage);

        model.addAttribute("selectedTab", "finalrout-tab");

        return "finalrouts";
    }

    @GetMapping(params = "list")
    @ResponseBody
    public String getListFinalRoutsByPage(@RequestParam("page_id") int pageId) {
        JsonArray jsonValues = new JsonArray();

        int total = 10;

        if (pageId != 1) {
            pageId= (pageId-1)*total+1;
        }

        List<FinalRout> finalRouts = finalRoutService.findAllByPage(pageId);
        Map<Long, LocalTime> mapDeparture = finalRoutService.getMapDeparture(finalRouts);
        Map<Long, LocalTime> mapArrival = finalRoutService.getMapArrival(finalRouts);

        JsonObject json;
        for (FinalRout finalRout : finalRouts) {
            json = new JsonObject();

            json.addProperty("id", finalRout.getId());
            json.addProperty("routName", finalRout.getRout().getRoutName());
            json.addProperty("trainName", finalRout.getTrain().getTrainName());
            json.addProperty("startStationName", finalRout.getRout().getStartStation().getStationName());
            json.addProperty("endStationName", finalRout.getRout().getEndStation().getStationName());
            json.addProperty("date", finalRout.getDate().toString());
            json.addProperty("departure",
                    (mapDeparture.get(finalRout.getId()) == null ? null : mapDeparture.get(finalRout.getId()).toString()));
            json.addProperty("arrival",
                    (mapArrival.get(finalRout.getId()) == null ? null : mapArrival.get(finalRout.getId()).toString()));
            jsonValues.add(json);
        }
        return jsonValues.toString();
    }

    @PostMapping(params = "change")
    @ResponseBody
    public FinalRout changeFinalRout(@RequestParam("finalRoutId") Long finalId) {
        return finalRoutService.findById(finalId);
    }

    @PostMapping(params = "delete")
    @ResponseBody
    public String deleteFinalRout(@RequestParam("finalRoutId") Long finalId) {
        finalRoutService.delete(finalId);

        return "Final route was deleted";
    }

    @PostMapping(params = "save")
    @ResponseBody
    public String saveFinalRout(@Valid @ModelAttribute("finalRoutForm") FinalRout finalRout,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "Fields are required";
        }

        finalRout.setTrain(trainService.findByName(finalRout.getTrain().getTrainName()));
        finalRout.setRout(routService.findByName(finalRout.getRout().getRoutName()));

        finalRoutService.save(finalRout);

        return "Final route " + finalRout.getRout().getRoutName() + " " + finalRout.getDate() + " was saved";
    }
}
