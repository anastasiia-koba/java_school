package system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import system.entity.Rout;
import system.entity.RoutSection;
import system.entity.Station;
import system.service.api.RoutSectionService;
import system.service.api.RoutService;
import system.service.api.StationService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * Controller for {@link system.entity.Station, system.entity.Rout}'s pages.
 */
@Secured(value={"ROLE_ADMIN"})
@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private StationService stationService;

    @Autowired
    private RoutSectionService routSectionService;

    @Autowired
    private RoutService routService;

    @GetMapping
    public String getAdminPage(Model model){
        model.addAttribute("stationForm", new Station());
        model.addAttribute("stations", stationService.findAll());

        model.addAttribute("sections", null);
        model.addAttribute("stationFrom", stationService.findAll());
        model.addAttribute("stationTo", stationService.findAll());

        model.addAttribute("selectedTab", "station-tab");

        return "admin";
    }

    @RequestMapping(value = "/routs", method = RequestMethod.POST)
    public String getRout(@ModelAttribute("stationFrom") Station startStation,
                               @ModelAttribute("stationTo") Station endStation,
                               Model model){
        startStation = stationService.findByName(startStation.getStationName());
        endStation = stationService.findByName(endStation.getStationName());

        List<Rout> routSet = routService.findByStartStationAndEndStation(startStation, endStation);
        Set<RoutSection> routSections = null;

        if (routSet == null){
            model.addAttribute("error", "Rout not found");
        }
        else {
            //TODO make unique search of rout
            routSections = routService.getRoutSectionInRout(routSet.get(0));
        }

        model.addAttribute("stationFrom", stationService.findAll());
        model.addAttribute("stationTo", stationService.findAll());
        model.addAttribute("sections", routSections);

        model.addAttribute("stations", stationService.findAll());
        model.addAttribute("stationForm", new Station());

        model.addAttribute("trains", trainService.findAll());
        model.addAttribute("trainForm", new Train());

        model.addAttribute("selectedTab", "rout-tab");

        return "admin";
    }

    @RequestMapping(value = "/stations", method = RequestMethod.POST, params = "change")
    public String changeStation(@ModelAttribute Station station, Model model) {
        Station stationForChange = stationService.findById(station.getId());
        model.addAttribute("stationForm", stationForChange);

        model.addAttribute("stations", stationService.findAll());

        model.addAttribute("stationFrom", stationService.findAll());
        model.addAttribute("stationTo", stationService.findAll());
        model.addAttribute("sections", null);

        model.addAttribute("selectedTab", "station-tab");

        return "admin";
    }

    @RequestMapping(value = "/stations", method = RequestMethod.POST, params = "delete")
    public String deleteStation(@ModelAttribute Station station, Model model) {
        Station stationForDelete = stationService.findById(station.getId());

        stationService.delete(stationForDelete);

        model.addAttribute("stationForm", new Station());
        model.addAttribute("stations", stationService.findAll());

        model.addAttribute("stationFrom", stationService.findAll());
        model.addAttribute("stationTo", stationService.findAll());
        model.addAttribute("sections", null);

        model.addAttribute("selectedTab", "station-tab");

        return "admin";
    }

    @RequestMapping(value = "/stations", method = RequestMethod.POST, params = "save")
    public String addStation(@Valid @ModelAttribute("stationForm") Station station,
                           BindingResult bindingResult, Model model) {
        model.addAttribute("stations", stationService.findAll());

        model.addAttribute("stationsFrom", stationService.findAll());
        model.addAttribute("stationsTo", stationService.findAll());
        model.addAttribute("sections", null);

        model.addAttribute("selectedTab", "station-tab");

        if (bindingResult.hasErrors()) {
            return "admin";
        }

        if (station.getId() != null){
            Station stationForChange = stationService.findById(station.getId());
            stationForChange.setStationName(station.getStationName());
            stationService.save(stationForChange);
        } else {
            stationService.create(station);
        }

        model.addAttribute("stationForm", new Station());
        model.addAttribute("stations", stationService.findAll());

        return "admin";
    }

    @RequestMapping(value = "/routs", method = RequestMethod.POST)
    public String getRout(@ModelAttribute("stationFrom") Station startStation,
                          @ModelAttribute("stationTo") Station endStation,
                          Model model){
        model.addAttribute("stations", stationService.findAll());
        model.addAttribute("stationForm", new Station());

        model.addAttribute("stationsFrom", stationService.findAll());
        model.addAttribute("stationsTo", stationService.findAll());
        model.addAttribute("routSectionForm", new RoutSection());
        model.addAttribute("rout", null);

        model.addAttribute("selectedTab", "rout-tab");

        startStation = stationService.findByName(startStation.getStationName());
        endStation = stationService.findByName(endStation.getStationName());

        List<Rout> routSet = routService.findByStartStationAndEndStation(startStation, endStation);
        Set<RoutSection> routSections = null;

        if (routSet == null){
            model.addAttribute("error", "Rout not found");
        }
        else {
            //TODO make unique search of rout
            routSections = routService.getRoutSectionInRout(routSet.get(0));
        }

        model.addAttribute("sections", routSections);
        model.addAttribute("rout", routSet.get(0));

        return "admin";
    }

    @RequestMapping(value = "/routs", method = RequestMethod.POST, params = "change")
    public String changeRoutSection(@ModelAttribute("section") RoutSection routSection,
                                    @RequestParam("routId") Long routId, Model model) {
        RoutSection sectionForChange = routSectionService.findById(routSection.getId());
        model.addAttribute("routSectionForm", sectionForChange);

        Rout rout = routService.findById(routId);

        model.addAttribute("stationForm", new Station());
        model.addAttribute("stations", stationService.findAll());

        model.addAttribute("stationsFrom", stationService.findAll());
        model.addAttribute("stationsTo", stationService.findAll());
        model.addAttribute("sections", routService.getRoutSectionInRout(rout));
        model.addAttribute("rout", rout);

        model.addAttribute("selectedTab", "rout-tab");

        return "admin";
    }

    @RequestMapping(value = "/routs", method = RequestMethod.POST, params = "delete")
    public String deleteRoutSection(@ModelAttribute RoutSection routSection,
                                    @RequestParam("routId") Long routId, Model model) {
        RoutSection sectionForDelete = routSectionService.findById(routSection.getId());

        routSectionService.delete(sectionForDelete);

        model.addAttribute("stationForm", new Station());
        model.addAttribute("stations", stationService.findAll());

        model.addAttribute("stationsFrom", stationService.findAll());
        model.addAttribute("stationsTo", stationService.findAll());
        model.addAttribute("routSectionForm", new RoutSection());

        Rout rout = routService.findById(routId);
        model.addAttribute("sections", routService.getRoutSectionInRout(rout));
        model.addAttribute("rout", rout);

        model.addAttribute("selectedTab", "train-tab");

        return "admin";
    }

    @RequestMapping(value = "/routs", method = RequestMethod.POST, params = "save")
    public String addRoutSection(@Valid @ModelAttribute("routSectionForm") RoutSection routSection,
                                 @RequestParam("routId") Long routId,
                                 BindingResult bindingResult, Model model) {
        model.addAttribute("stationForm", new Station());
        model.addAttribute("stations", stationService.findAll());

        model.addAttribute("stationsFrom", stationService.findAll());
        model.addAttribute("stationsTo", stationService.findAll());

        Rout rout = routService.findById(routId);
        model.addAttribute("rout", rout);
        model.addAttribute("sections", routService.getRoutSectionInRout(rout));

        model.addAttribute("selectedTab", "rout-tab");

        if (bindingResult.hasErrors()) {
            return "admin";
        }

        if (train.getId() != null){
            Train trainForChange = trainService.findById(train.getId());
            trainForChange.setTrainName(train.getTrainName());
            trainForChange.setPlacesNumber(train.getPlacesNumber());
            trainService.save(trainForChange);
        } else {
            trainService.create(train);
        }

        model.addAttribute("trainForm", new Train());
        model.addAttribute("trains", trainService.findAll());

        return "admin";
    }
}
